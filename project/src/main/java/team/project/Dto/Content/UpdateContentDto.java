package team.project.Dto.Content;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
public class UpdateContentDto {
    @NotBlank
    private String title;
    @NotBlank
    private String text;
}
