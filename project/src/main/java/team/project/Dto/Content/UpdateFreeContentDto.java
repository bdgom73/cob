package team.project.Dto.Content;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UpdateFreeContentDto {
    @NotBlank
    private String title;
    @NotBlank
    private String text;
    private Boolean notice;
}
