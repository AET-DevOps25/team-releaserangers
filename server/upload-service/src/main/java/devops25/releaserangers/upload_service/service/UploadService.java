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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
            File fileEntity = new File();
            fileEntity.setFilename(file.getOriginalFilename());
            fileEntity.setContentType(file.getContentType());
            fileEntity.setData(file.getBytes());
            fileEntity.setCourseId(courseId);
            fileRepository.save(fileEntity);
            uploadedFiles.add(fileEntity);
        }

        forwardFilesToSummaryService(courseId);
        return uploadedFiles;
    }

    public List<FileMetadataDTO> getAllFiles() {
        return fileRepository.findAll().stream()
            .map(f -> new FileMetadataDTO(
                f.getId(),
                f.getFilename(),
                f.getContentType(),
                f.getCourseId(),
                f.getUploadedAt()
            ))
            .collect(Collectors.toList());
    }

    public void deleteAllFiles() {
        fileRepository.deleteAll();
    }

    public void forwardFilesToSummaryService(String courseId) {
        List<File> existingFiles = fileRepository.findByCourseId(courseId);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
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