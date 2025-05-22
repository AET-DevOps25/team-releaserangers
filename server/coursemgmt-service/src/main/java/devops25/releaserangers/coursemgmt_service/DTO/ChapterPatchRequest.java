package devops25.releaserangers.coursemgmt_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterPatchRequest {
    private String title;
    private String content;
    private String emoji;
    private Boolean isFavorite;

}
