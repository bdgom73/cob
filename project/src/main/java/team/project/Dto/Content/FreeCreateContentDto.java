package team.project.Dto.Content;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.project.Entity.Progress;

@Data
@AllArgsConstructor
public class FreeCreateContentDto {

    private String title;
    private String text;
    private Long teamId;
    private Boolean notice;

}
