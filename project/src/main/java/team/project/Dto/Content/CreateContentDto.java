package team.project.Dto.Content;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class CreateContentDto {

    @NotBlank
    private String title;
    private String text;
    @NotNull
    private Long projectId;
    @NotNull
    private Long categoryId;

}
