package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.DeveloperRole;
import team.project.Entity.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class JoinTeam {

    @Id @GeneratedValue
    @Column(name = "join_team_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private JoinState joinState;

    private LocalDateTime joinDate;

    private TeamRole teamRole;

    private DeveloperRole developerRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public JoinTeam() {

    }

    public void apply(Team team , Member member){
        this.team = team;
        team.getJoinTeams().add(this);
        this.member = member;
        joinState = JoinState.WAITING;
        joinDate = LocalDateTime.now();
    }

    public void changeState(JoinState state){
        joinState = state;
        joinDate = LocalDateTime.now();
    }


    public void changeDeveloperRole(DeveloperRole role){
        developerRole = role;
    }

    public void changeRole(TeamRole role){
        teamRole = role;
    }

    public static JoinTeam applyTeam(Team team , Member member){
        JoinTeam joinTeam = new JoinTeam();
        joinTeam.apply(team, member);
        joinTeam.changeRole(TeamRole.APPLICANT);
        joinTeam.changeDeveloperRole(DeveloperRole.None);
        return joinTeam;
    }
}
