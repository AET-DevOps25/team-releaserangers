package devops25.releaserangers.upload_service.controller;

import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping
    public ResponseEntity<?> uploadMultipleFiles(
            @RequestParam("file") MultipartFile[] files,
            @RequestParam("courseId") String courseId
    ) {
        try {
            List<File> uploadedFiles = uploadService.handleUploadedFiles(files, courseId);
            List<FileMetadataDTO> dtos = uploadedFiles.stream().map(upload -> new FileMetadataDTO(
                    upload.getId(),
                    upload.getFilename(),
                    upload.getContentType(),
                    upload.getCourseId(),
                    upload.getUploadedAt()
            )).toList();
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process files: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFiles() {
        try {
            return ResponseEntity.ok(uploadService.getAllFiles());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch files: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllFiles() {
        try {
            uploadService.deleteAllFiles();
            return ResponseEntity.ok().body(List.of());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete files: " + e.getMessage());
        }
    }
}
