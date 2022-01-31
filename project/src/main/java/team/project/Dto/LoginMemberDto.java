package team.project.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginMemberDto {

    private Long memberId;
    private String email;
    private String name;
    private String nickname;

}
