package devops25.releaserangers.upload_service.controller;

import devops25.releaserangers.upload_service.dto.FileMetadataDTO;
import devops25.releaserangers.upload_service.model.File;
import devops25.releaserangers.upload_service.service.UploadService;
import devops25.releaserangers.upload_service.util.AuthUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
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
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Exposing service references is acceptable here")
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
        final long startTime = System.nanoTime();
        if (token == null) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            uploadService.updateLatency(startTime);
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
            uploadService.updateLatency(startTime);
            return ResponseEntity.ok(dtos);
        } catch (IllegalArgumentException e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process files: " + e.getMessage());
        } catch (Exception e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllFiles(@CookieValue(value = "token", required = false) String token) {
        final long startTime = System.nanoTime();
        if (token == null) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }

        try {
            final List<FileMetadataDTO> files = uploadService.getAllFiles();
            if (files.isEmpty()) {
                uploadService.updateLatency(startTime);
                return ResponseEntity.noContent().build();
            }
            uploadService.updateLatency(startTime);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch files: " + e.getMessage());
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<?> getFilesByCourseId(@CookieValue(value = "token", required = false) String token, @PathVariable String courseId) {
        final long startTime = System.nanoTime();
        if (token == null) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }

        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        try {
            final List<FileMetadataDTO> files = uploadService.getFilesByCourseId(courseId);
            if (files.isEmpty()) {
                uploadService.updateLatency(startTime);
                return ResponseEntity.noContent().build();
            }
            uploadService.updateLatency(startTime);
            return ResponseEntity.ok(files);
        } catch (Exception e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to fetch files for course: " + e.getMessage());
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteAllFiles(@CookieValue(value = "token", required = false) String token) {
        final long startTime = System.nanoTime();
        if (token == null) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }

        try {
            uploadService.deleteAllFiles();
            uploadService.updateLatency(startTime);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete files: " + e.getMessage());
        }
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> deleteFilesByCourseId(@CookieValue(value = "token", required = false) String token, @PathVariable String courseId) {
        final long startTime = System.nanoTime();
        if (token == null) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).build();
        }
        final Optional<String> userIDOpt = authUtils.validateAndGetUserId(token);
        if (userIDOpt.isEmpty()) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(401).body(null);
        }
        try {
            uploadService.deleteFilesByCourseId(courseId);
            uploadService.updateLatency(startTime);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            uploadService.updateLatency(startTime);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete files for course: " + e.getMessage());
        }
    }
}
