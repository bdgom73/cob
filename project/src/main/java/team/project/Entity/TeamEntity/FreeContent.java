package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.BaseEntity;
import team.project.Entity.Member;
import team.project.Entity.Progress;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class FreeContent extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "content_id")
    private Long id;

    private String title;
    @Column(columnDefinition = "LONGTEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private Boolean notice;

    @OneToMany(mappedBy = "content",cascade = CascadeType.ALL)
    private List<FreeComments> comments = new ArrayList<>();

    protected FreeContent(){}

    public FreeContent(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public void setTeam(Team team){
        this.team = team;
    }
    public void setMember(Member member){
        this.member = member;
    }

    public void setNotice(Boolean notice) {
        this.notice = notice;
    }

    public void updateContent(String title, String text){
        this.title = title;
        this.text = text;
    }
}
