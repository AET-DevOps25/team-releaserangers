package devops25.releaserangers.upload_service.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class FileMetadataDTO {
    private final String id;
    private final String filename;
    private final String contentType;
    private final String courseId;
    private final LocalDateTime uploadedAt;
}

