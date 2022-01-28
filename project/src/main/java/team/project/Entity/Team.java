package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Team extends BaseEntity{

    @Id @GeneratedValue
    @Column(name = "team_id")
    private Long id;
    private String name;
    private String introduction;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<Project> projects = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    private List<JoinTeam> joinTeams = new ArrayList<>();

    protected Team(){

    }

    public Team(String name, String introduction) {
        this.name = name;
        this.introduction = introduction;
    }

    public void setMember(Member member){
        this.member = member;
    }


    public static Team createTeam(String name, String introduction, Member member){
        Team team = new Team(name, introduction);
        team.setMember(member);
        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        joinTeam.stateChange(JoinState.OK);
        return team;
    }

}
