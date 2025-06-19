package devops25.releaserangers.upload_service.service;

import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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

@Service
public class UploadService {
    private final FileRepository fileRepository;
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf");

    @Value("${summary.service.url}")
    private String summaryServiceUrl;

    public UploadService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Transactional
    public List<File> handleUploadedFiles(MultipartFile[] files, String courseId) throws IOException {
        if (files == null || files.length == 0) {
            throw new IllegalArgumentException("No files provided. Please upload at least one PDF file.");
        }
        for (MultipartFile file : files) {
            if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
                throw new IllegalArgumentException("Currently only PDF file(s) are allowed. Please upload valid PDF file(s).");
            }
        }
        List<File> uploadedFiles = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null) {
                throw new IllegalArgumentException("File name cannot be null.");
            }
            File existingFile = fileRepository.findByFilename(originalFilename);
            byte[] fileBytes = file.getBytes();
            if (existingFile == null) {
                // No file with this name exists, save as is
                uploadedFiles.add(saveFile(originalFilename, file.getContentType(), fileBytes, courseId));
                System.out.println(originalFilename + " has been uploaded.");
            } else if (Arrays.equals(existingFile.getData(), fileBytes)) {
                // File exists and content is the same, update
                System.out.println("File with name " + existingFile.getFilename() + " already exists and has the same content. Updating the existing file.");
                fileRepository.updateFile(existingFile.getId(), originalFilename, file.getContentType(), fileBytes, courseId);
                System.out.println(originalFilename + " has been updated.");
                uploadedFiles.add(fileRepository.findByFilename(originalFilename));
            } else {
                // File exists but content is different, save with unique name
                System.out.println(Arrays.toString(existingFile.getData()) + "\n\n");
                System.out.println(Arrays.toString(fileBytes) + "\n\n");
                String uniqueFilename = generateUniqueFilename(originalFilename);
                uploadedFiles.add(saveFile(uniqueFilename, file.getContentType(), fileBytes, courseId));
                System.out.println(uniqueFilename + " has been uploaded.");
            }
        }
        forwardFilesToSummaryService(courseId);
        return uploadedFiles;
    }

    private File saveFile(String filename, String contentType, byte[] data, String courseId) {
        File fileEntity = new File();
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
        int dotIndex = originalFilename.lastIndexOf('.');
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

    public void deleteAllFiles() {
        fileRepository.deleteAll();
    }

    public void deleteFilesByCourseId(String courseId) {
        List<File> files = fileRepository.findByCourseIdWithoutData(courseId);
        fileRepository.deleteAll(files);
    }

    public void forwardFilesToSummaryService(String courseId) {
        List<File> existingFiles = fileRepository.findByCourseId(courseId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("courseId", courseId);
        for (File file : existingFiles) {
            body.add("file", new ByteArrayResource(file.getData()) {
                @Override
                public String getFilename() {
                    return file.getFilename();
                }
            });
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(summaryServiceUrl, requestEntity, String.class);
    }
}