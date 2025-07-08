package devops25.releaserangers.upload_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handling file uploads, validation, and communication with external services.
 */
@Service
public class UploadService {
    private final FileRepository fileRepository;
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf");
    private static final String NO_FILES_ERROR = "No files provided. Please upload at least one PDF file.";
    private static final String INVALID_TYPE_ERROR = "Currently only PDF file(s) are allowed. Please upload valid PDF file(s).";
    private static final String NULL_FILENAME_ERROR = "File name cannot be null.";
    private static final String TOKEN_COOKIE = "token=";
    private static final String ERROR_FETCHING_CHAPTERS = "Error fetching chapters from coursemgmt-service: ";
    private static final String ERROR_SERIALIZING_CHAPTERS = "Error serializing chapters response: ";

    @Value("${summary.service.url}")
    private String summaryServiceUrl;

    @Value("${coursemgmt.service.url}")
    private String courseMgmtServiceUrl;

    /**
     * Constructs the UploadService with the required FileRepository.
     *
     * @param fileRepository the repository for file persistence
     */
    public UploadService(final FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * Handles uploaded files: validates, saves, and processes them.
     *
     * @param files   the uploaded files
     * @param courseId the course identifier
     * @param token   the authentication token
     * @return list of uploaded File entities
     * @throws IOException if file processing fails
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public List<File> handleUploadedFiles(final MultipartFile[] files, final String courseId, final String token) throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException(NO_FILES_ERROR);
        }
        for (final MultipartFile file : files) {
            if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
                throw new IllegalArgumentException(INVALID_TYPE_ERROR);
            }
        }
        final List<File> uploadedFiles = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            final String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException(NULL_FILENAME_ERROR);
            }
            final File existingFile = fileRepository.findByFilename(originalFilename);
            final byte[] fileBytes = file.getBytes();
            if (existingFile == null) {
                // No file with this name exists, save as is
                uploadedFiles.add(saveFile(originalFilename, file.getContentType(), fileBytes, courseId));
                logger.info("{} has been uploaded.", originalFilename);
            } else if (Arrays.equals(existingFile.getData(), fileBytes)) {
                // File exists and content is the same, update
                logger.info("File with name {} already exists and has the same content. Updating the existing file.", existingFile.getFilename());
                fileRepository.updateFile(existingFile.getId(), originalFilename, file.getContentType(), fileBytes, courseId);
                logger.info("{} has been updated and uploaded.", originalFilename);
                uploadedFiles.add(fileRepository.findByFilename(originalFilename));
            } else {
                // File exists but content is different, save with unique name
                final String uniqueFilename = generateUniqueFilename(originalFilename);
                uploadedFiles.add(saveFile(uniqueFilename, file.getContentType(), fileBytes, courseId));
                logger.info("{} was renamed to + {} and has been uploaded.", originalFilename, uniqueFilename);
            }
        }
        forwardFilesToSummaryService(uploadedFiles, courseId, token);
        return uploadedFiles;
    }

    private File saveFile(String filename, String contentType, byte[] data, String courseId) {
        final File fileEntity = new File();
        fileEntity.setFilename(filename);
        fileEntity.setContentType(contentType);
        fileEntity.setData(data);
        fileEntity.setCourseId(courseId);
        fileRepository.save(fileEntity);
        return fileEntity;
    }

    private String generateUniqueFilename(String originalFilename) {
        int suffix = 1;
        String baseName = originalFilename;
        String extension = "";
        final int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = originalFilename.substring(0, dotIndex);
            extension = originalFilename.substring(dotIndex);
        }
        String candidate = originalFilename;
        while (fileRepository.findByFilename(candidate) != null) {
            candidate = baseName + "_" + suffix + extension;
            suffix++;
        }
        return candidate;
    }

    /**
     * Retrieves metadata for all files.
     *
     * @return list of FileMetadataDTO objects
     */
    public List<FileMetadataDTO> getAllFiles() {
        return fileRepository.findAll().stream()
            .map(f -> new FileMetadataDTO(
                f.getId(),
                f.getFilename(),
                f.getContentType(),
                f.getCourseId(),
                f.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    /**
     * Retrieves metadata for files associated with a specific course.
     *
     * @param courseId the course identifier
     * @return list of FileMetadataDTO objects
     */
    public List<FileMetadataDTO> getFilesByCourseId(String courseId) {
        return fileRepository.findByCourseIdWithoutData(courseId).stream()
                .map(f -> new FileMetadataDTO(
                        f.getId(),
                        f.getFilename(),
                        f.getContentType(),
                        f.getCourseId(),
                        f.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all files.
     */
    public void deleteAllFiles() {
        fileRepository.deleteAll();
    }

    /**
     * Deletes files associated with a specific course.
     *
     * @param courseId the course identifier
     */
    public void deleteFilesByCourseId(String courseId) {
        final List<File> files = fileRepository.findByCourseId(courseId);
        fileRepository.deleteAll(files);
    }

    /**
     * Forwards uploaded files and existing chapter summaries to the summary service.
     *
     * @param uploadedFiles the list of uploaded File entities
     * @param courseId      the course identifier
     * @param token         the authentication token
     */
    public void forwardFilesToSummaryService(final List<File> uploadedFiles, final String courseId, final String token) {
        final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("courseId", courseId);
        for (File file : uploadedFiles) {
            body.add("file", new ByteArrayResource(file.getData()) {
                @Override
                public String getFilename() {
                    return file.getFilename();
                }
            });
        }

        final String chaptersJson = fetchChaptersJson(courseId, token);
        logger.info("Chapters: {}", chaptersJson);
        body.add("existingChapterSummary", chaptersJson);

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        final RestTemplate summaryRestTemplate = new RestTemplate();
        summaryRestTemplate.postForEntity(summaryServiceUrl, requestEntity, String.class);
    }

    /**
     * Fetches chapters for a given course from the course management service.
     *
     * @param courseId the course identifier
     * @param token    the authentication token
     * @return the chapters as a JSON string
     */
    private String fetchChaptersJson(final String courseId, final String token) {
        final RestTemplate restTemplate = new RestTemplate();
        final String chaptersUrl = String.format("%s/courses/%s/chapters", courseMgmtServiceUrl, courseId);
        logger.info("Getting chapters from: {}", chaptersUrl);
        final HttpHeaders getHeaders = new HttpHeaders();
        getHeaders.set("Cookie", TOKEN_COOKIE + token);
        final HttpEntity<Void> getEntity = new HttpEntity<>(getHeaders);

        ResponseEntity<Object[]> chaptersResponse;
        try {
            chaptersResponse = restTemplate.exchange(
                    chaptersUrl,
                    org.springframework.http.HttpMethod.GET,
                    getEntity,
                    Object[].class
            );
        } catch (Exception e) {
            logger.error(ERROR_FETCHING_CHAPTERS, e);
            chaptersResponse = ResponseEntity.ok(new Object[0]);
        }

        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(chaptersResponse.getBody());
        } catch (Exception e) {
            logger.error(ERROR_SERIALIZING_CHAPTERS, e);
            return "[]";
        }
    }
}