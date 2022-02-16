package team.project.Dto.Content;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.project.Entity.Progress;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CreateContentDto {

    private String title;
    private String text;
    private Long projectId;
    private Progress category;

}
