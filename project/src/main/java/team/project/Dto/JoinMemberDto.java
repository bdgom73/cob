package team.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinMemberDto {

    private String email;
    private String password1;
    private String password2;
    private String name;
    private String nickname;

}
