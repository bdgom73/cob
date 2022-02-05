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

//    @OneToOne(mappedBy = "team", cascade = CascadeType.ALL)
//    private ProjectCategory projectCategory;

    protected Team(){

    }

    public Team(String name, String introduction) {
        this.name = name;
        this.introduction = introduction;
    }

    public void setMember(Member member){
        this.member = member;
    }
//    public void setProjectCategory(ProjectCategory category){
//        projectCategory = category;
//    }

    public void changeTeam(String name, String introduction){
        this.name = name;
        this.introduction = introduction;
    }
    public static Team createTeam(String name, String introduction, Member member){
        Team team = initTeam(name, introduction, member);
        return team;
    }


    public static Team createTeam(String name, String introduction, Member member, ProjectCategory category){
        Team team = initTeam(name, introduction, member);
        category.setTeam(team);
        return team;
    }

    private static Team initTeam(String name, String introduction, Member member) {
        Team team = new Team(name, introduction);
        team.setMember(member);
        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        joinTeam.changeRole(TeamRole.LEADER);
        joinTeam.changeState(JoinState.OK);
        return team;
    }
}
