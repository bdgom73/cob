package team.project.Controller.Form.TeamForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamForm {
    @NotBlank
    @Size(min = 2)
    private String name;
    private String introduction;
}
