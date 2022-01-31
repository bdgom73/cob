package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class ProjectCategory {

    @Id @GeneratedValue
    @Column(name = "project_category_id")
    private Long id;
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public ProjectCategory(String name) {
        this.name = name;
    }

    public void setTeam(Team team){
        this.team = team;
        team.setProjectCategory(this);
    }
}
