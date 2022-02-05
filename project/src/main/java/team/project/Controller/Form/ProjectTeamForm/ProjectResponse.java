package team.project.Controller.Form.ProjectTeamForm;

import lombok.AllArgsConstructor;
import lombok.Data;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.Team;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectResponse {
    private Long projectId;
    private String projectTitle;
    private String projectIntroduction;
    private LocalDateTime projectStartDate;
    private LocalDateTime projectEndDate;
    private Progress projectProgress;
    private Long memberId;
    private String memberName;
    private String memberNickname;
}
