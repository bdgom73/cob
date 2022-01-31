package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Project {

    @Id @GeneratedValue
    @Column(name = "project_id")
    private Long id;

    private String title;
    private String introduction;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private Progress progress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<ProjectMember> projectMembers = new ArrayList<>();


    protected Project(){}

    public Project(String title,LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project(String title, String introduction, LocalDateTime startDate, LocalDateTime endDate) {
        this.title = title;
        this.introduction = introduction;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void setTeam(Team team){
        this.team = team;
        team.getProjects().add(this);
    }
    public void setMember(Member member){
        this.member = member;
    }
    public void setProgress(Progress progress){
        this.progress = progress;
    }
    public static Project create(String title, String introduction , LocalDateTime startDate, LocalDateTime endDate, Team team, Member member){
        Project project = new Project(title,introduction,startDate,endDate);
        project.setTeam(team);
        project.setMember(member);
        project.setProgress(Progress.Requirements_Analysis);
        return project;
    }



}
