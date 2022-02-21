package team.project.Controller.Form.TeamForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamForm {

    @Size(min = 2)
    @NotBlank
    private String name;
    @NotNull
    private String introduction;
}
