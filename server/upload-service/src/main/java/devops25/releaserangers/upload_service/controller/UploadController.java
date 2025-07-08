package devops25.releaserangers.upload_service.controller;

import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.service.UploadService;
import devops25.releaserangers.upload_service.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @Autowired
    private AuthUtils authUtils;

    @PostMapping("/{courseId}")
    public ResponseEntity<?> uploadMultipleFiles(
            @CookieValue(value = "token", required = false) String token,
            @RequestParam("files") MultipartFile[] files,
            @PathVariable("courseId") String courseId
    ) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            final List<File> uploadedFiles = uploadService.handleUploadedFiles(files, courseId, token);
            final List<FileMetadataDTO> dtos = uploadedFiles.stream().map(upload -> new FileMetadataDTO(
                    upload.getId(),
                    upload.getFilename(),
                    upload.getContentType(),
                    upload.getCourseId(),
                    upload.getCreatedAt()
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
    public ResponseEntity<?> getAllFiles(@CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            final List<FileMetadataDTO> files = uploadService.getAllFiles();
            if (files.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch files: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getFilesByCourseId(@CookieValue(value = "token", required = false) String token, @PathVariable String courseId) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }

        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            final List<FileMetadataDTO> files = uploadService.getFilesByCourseId(courseId);
            if (files.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch files for course: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllFiles(@CookieValue(value = "token", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }

        try {
            uploadService.deleteAllFiles();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete files: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteFilesByCourseId(@CookieValue(value = "token", required = false) String token, @PathVariable String courseId) {
        if (token == null) {
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            return ResponseEntity.status(401).body(null);
        }
        try {
            uploadService.deleteFilesByCourseId(courseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete files for course: " + e.getMessage());
        }
    }
}
