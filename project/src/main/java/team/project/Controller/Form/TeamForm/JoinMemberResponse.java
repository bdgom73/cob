package team.project.Controller.Form.TeamForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.project.Entity.JoinState;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class JoinMemberResponse {
    private Long joinTeamId;
    private Long memberId;
    private String memberName;
    private String memberNickname;
    private JoinState joinState;
    private LocalDateTime joinDate;
}
