package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.BaseEntity;
import team.project.Entity.Member;
import team.project.Entity.ProjectCategory;
import team.project.Entity.TeamEntity.Project;

import javax.persistence.*;

@Entity
@Getter
public class Content extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    private String title;
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_category_id")
    private ProjectCategory projectCategory;

    protected Content() {

    }

    public Content(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public void setProject(Project project){
        this.project = project;
    }
    public void setMember(Member member){
        this.member = member;
    }
    public void setCategory(ProjectCategory category){
        projectCategory = category;
    }

    public void updateContent(String title, String text){
        this.title = title;
        this.text = text;
    }
}
