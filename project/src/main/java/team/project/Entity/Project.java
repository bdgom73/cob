package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

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

    public static Project create(String title, String introduction , LocalDateTime startDate, LocalDateTime endDate, Team team){
        Project project = new Project(title,introduction,startDate,endDate);
        project.setTeam(team);
        return project;
    }


}
