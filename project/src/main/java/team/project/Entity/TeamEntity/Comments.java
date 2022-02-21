package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.BaseEntity;
import team.project.Entity.Member;

import javax.persistence.*;

@Entity
@Getter
public class Comments extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "comments_id")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="content_id")
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id")
    private Member member;

    protected Comments(){}

    public Comments(String text, Content content, Member member) {
        this.text = text;
        this.content = content;
        this.member = member;
        content.getComments().add(this);
    }

    public void changeText(String text){
        this.text = text;
    }
}
