package devops25.releaserangers.upload_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.Setter;
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
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

/**
 * Service for handling file uploads, validation, and communication with external services.
 */
@Service
public class UploadService {
    private final FileRepository fileRepository;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(UploadService.class);
  
    private static final List<String> ALLOWED_TYPES = List.of("application/pdf");
    private static final String NO_FILES_ERROR = "No files provided. Please upload at least one PDF file.";
    private static final String INVALID_TYPE_ERROR = "Currently only PDF file(s) are allowed. Please upload valid PDF file(s).";
    private static final String NULL_FILENAME_ERROR = "File name cannot be empty or null.";
    private static final String TOKEN_COOKIE = "token=";
    private static final String ERROR_FETCHING_CHAPTERS = "Error fetching chapters from coursemgmt-service: ";
    private static final String ERROR_SERIALIZING_CHAPTERS = "Error serializing chapters response: ";

    @Value("${summary.service.url}")
    private String summaryServiceUrl;

    @Value("${coursemgmt.service.url}")
    private String courseMgmtServiceUrl;

    private final MeterRegistry uploadRegistry;
    private final Counter uploadServiceRequestCounter;
    private final Counter uploadErrorTotal;
    private final Counter summaryCounter;
    private final Gauge uploadErrorGauge;
    private final Timer summaryTimer;

    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Gauge is used to track latency")
    private Gauge uploadLatencyGauge;
    @SuppressFBWarnings(value = "URF_UNREAD_FIELD", justification = "Gauge is used to track summary duration")
    private Gauge summaryDurationGauge;
    @Getter
    @Setter
    private volatile double currentLatency = 0.0;
    @Getter
    @Setter
    private volatile double lastSummaryDuration = 0.0;

    private final ConcurrentLinkedQueue<Instant> errorTimestamps = new ConcurrentLinkedQueue<>();
    private static final Duration ERROR_EXPIRY = Duration.ofMinutes(10);


    /**
     * Constructs an UploadService with the specified dependencies.
     *
     * @param fileRepository the repository for file operations
     * @param restTemplate   the RestTemplate for making HTTP requests
     * @param uploadRegistry       the MeterRegistry for metrics
     */
    @SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
    public UploadService(final FileRepository fileRepository, RestTemplate restTemplate, MeterRegistry uploadRegistry) {

        this.uploadRegistry = uploadRegistry;
        this.uploadRegistry.config().commonTags("service", "upload-service");

        this.fileRepository = fileRepository;
        this.restTemplate = restTemplate;

        this.uploadServiceRequestCounter = Counter.builder("upload_service_requests_total")
                .description("Total number of upload requests")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
        this.summaryCounter = Counter.builder("upload_service_summary_requests_total")
                .description("Total number of requests to the summary service")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
        this.uploadErrorGauge = Gauge.builder("upload_service_errors_gauge", errorTimestamps, queue -> {
            final Instant now = Instant.now();
            // Remove expired timestamps
            queue.removeIf(ts -> ts.isBefore(now.minus(ERROR_EXPIRY)));
            return (double) queue.size();
        })
        .description("Number of errors when processing uploaded files (expires after 10 minutes)")
        .tags("service", "upload-service")
        .register(this.uploadRegistry);
        this.uploadErrorTotal = Counter.builder("upload_service_errors_total")
                .description("Total number of errors when processing uploaded files")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
        this.summaryTimer = Timer.builder("summary_service_request_duration")
                .description("Time taken to get uploaded files summarized")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
        this.uploadLatencyGauge = Gauge.builder("upload_service_current_latency", this, UploadService::getCurrentLatency)
                .description("Current latency of the latest upload request (ms)")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
        this.summaryDurationGauge = Gauge.builder("upload_service_last_summary_duration", this, UploadService::getLastSummaryDuration)
                .description("Duration of the last summary service request (ms)")
                .tags("service", "upload-service")
                .register(this.uploadRegistry);
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
        logger.info("Handle uploaded files");
        uploadServiceRequestCounter.increment();
        if (files == null || files.length == 0) {
            errorTimestamps.add(Instant.now());
            uploadErrorTotal.increment();
            logger.info(NO_FILES_ERROR);
            throw new IllegalArgumentException(NO_FILES_ERROR);
        }
        for (final MultipartFile file : files) {
            if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
                errorTimestamps.add(Instant.now());
                uploadErrorTotal.increment();
                logger.info(INVALID_TYPE_ERROR);
                throw new IllegalArgumentException(INVALID_TYPE_ERROR);
            }
        }
        final List<File> uploadedFiles = new java.util.ArrayList<>();
        for (MultipartFile file : files) {
            final String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                errorTimestamps.add(Instant.now());
                uploadErrorTotal.increment();
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
                logger.info("{} was renamed to {} and has been uploaded.", originalFilename, uniqueFilename);
            }
        }
        forwardFilesToSummaryService(uploadedFiles, courseId, token);
        return uploadedFiles;
    }

    private File saveFile(String filename, String contentType, byte[] data, String courseId) {
        uploadServiceRequestCounter.increment();
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
        uploadServiceRequestCounter.increment();
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
        uploadServiceRequestCounter.increment();
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
        uploadServiceRequestCounter.increment();
        fileRepository.deleteAll();
    }

    /**
     * Deletes files associated with a specific course.
     *
     * @param courseId the course identifier
     */
    public void deleteFilesByCourseId(String courseId) {
        uploadServiceRequestCounter.increment();
        final List<File> files = fileRepository.findByCourseIdWithoutData(courseId);
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
        summaryCounter.increment();
        final long startTime = System.nanoTime();
        summaryTimer.record(() -> {
            final MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("courseId", courseId);
            for (File file : uploadedFiles) {
                body.add("files", new ByteArrayResource(file.getData()) {
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
            if (token != null) {
                headers.add(HttpHeaders.COOKIE, "token=" + token);
            }
            final HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(summaryServiceUrl, requestEntity, String.class);
        });
        updateSummaryDuration(startTime);
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

    public void updateLatency(long startTime) {
        final long latency = System.nanoTime() - startTime;
        setCurrentLatency(latency / 1_000_000.0);
    }

    private void updateSummaryDuration(long startTime) {
        final long duration = System.nanoTime() - startTime;
        setLastSummaryDuration(duration / 1_000_000_000.0); // Convert to seconds
    }
}
