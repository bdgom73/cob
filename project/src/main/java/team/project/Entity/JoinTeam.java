package team.project.Entity;

import lombok.Getter;

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

    public void changeRole(TeamRole role){
        teamRole = role;
    }

    public static JoinTeam applyTeam(Team team , Member member){
        JoinTeam joinTeam = new JoinTeam();
        joinTeam.apply(team, member);
        joinTeam.changeRole(TeamRole.APPLICANT);
        return joinTeam;
    }
}
