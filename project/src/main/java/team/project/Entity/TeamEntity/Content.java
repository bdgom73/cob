package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.BaseEntity;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.ProjectCategory;
import team.project.Entity.TeamEntity.Project;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Content extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private Progress category;

    @OneToMany(mappedBy = "content",cascade = CascadeType.ALL)
    private List<Comments> comments = new ArrayList<>();

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
    public void setCategory(Progress category){
        this.category = category;
    }

    public void updateContent(String title, String text){
        this.title = title;
        this.text = text;
    }
}
