package devops25.releaserangers.upload_service.service;

import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.repository.FileRepository;
import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UploadService {
    private final FileRepository fileRepository;
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "application/pdf", "text/markdown"
    );

    public UploadService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public File handleFileUpload(MultipartFile file, String courseId) throws IOException {
        if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Only PDF or Markdown files are allowed.");
        }
        File fileEntity = new File();
        fileEntity.setFilename(file.getOriginalFilename());
        fileEntity.setContentType(file.getContentType());
        fileEntity.setData(file.getBytes());
        fileEntity.setCourseId(courseId);
        fileRepository.save(fileEntity);

        // Forward to summary service
        /*
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });
        HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        restTemplate.postForEntity("http://localhost/summarize", requestEntity, String.class);
        */

        return fileEntity;
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
}