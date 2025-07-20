package devops25.releaserangers.coursemgmt_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteItemDto {
    private String id;
    private String type; // "course" or "chapter"
    private String courseId; // Only for chapters
    private String title;
    private String emoji;
}
