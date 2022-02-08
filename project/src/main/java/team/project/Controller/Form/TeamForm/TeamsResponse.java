package team.project.Controller.Form.TeamForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.project.Entity.TeamEntity.Team;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TeamsResponse {

    private Long teamId;
    private String name;
    private String introduction;
    private Long leaderId;
    private String leaderNickname;
    private String leaderName;
    private String leaderEmail;
    private LocalDateTime createDate;
    private LocalDateTime modifiedDate;

    public TeamsResponse(Team team) {
        teamId = team.getId();
        name = team.getName();
        introduction = team.getIntroduction();
        leaderId = team.getMember().getId();
        leaderNickname = team.getMember().getNickname();
        leaderName = team.getMember().getName();
        leaderEmail = team.getMember().getEmail();
        createDate = team.getCreatedDate();
        modifiedDate = team.getModifiedDate();
    }

}
