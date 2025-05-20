package devops25.releaserangers.upload_service.controller;

import devops25.releaserangers.upload_service.model.Upload;
import devops25.releaserangers.upload_service.repository.UploadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadRepository uploadRepository;

    @PostMapping
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            if (!Objects.equals(file.getContentType(), "application/pdf")) {
                return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                        .body("Only PDF files are supported.");
            }
            Upload upload = new Upload();
            upload.setFilename(file.getOriginalFilename());
            upload.setContentType(file.getContentType());
            upload.setData(file.getBytes());
            uploadRepository.save(upload);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to upload file: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<String> getAllUploads() {
        try {
            StringBuilder response = new StringBuilder();
            for (Upload upload : uploadRepository.findAll()) {
                response.append("File Name: ").append(upload.getFilename()).append("\n");
                response.append("Content Type: ").append(upload.getContentType()).append("\n");
                response.append("Data Size: ").append(upload.getData().length).append(" bytes\n\n");
            }
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to retrieve files: " + e.getMessage());
        }
    }
}

