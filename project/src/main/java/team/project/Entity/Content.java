package team.project.Entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Content extends BaseEntity{

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
    private ProjectCategory category;
}
