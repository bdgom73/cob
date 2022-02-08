package team.project.Entity;

import lombok.Getter;
import team.project.Entity.TeamEntity.Project;

import javax.persistence.*;

@Entity
@Getter
public class ProjectMember {

    @Id @GeneratedValue
    @Column(name = "project_member_id")
    private Long id;

    private String role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectMember(String role, Member member, Project project) {
        this.role = role;
        this.member = member;
        this.project = project;
    }

    public void changeRole(String role){
        this.role = role;
    }

    public static ProjectMember createProject(String role, Member member, Project project){
        ProjectMember projectMember = new ProjectMember(role, member, project);
        return projectMember;
    }
}
