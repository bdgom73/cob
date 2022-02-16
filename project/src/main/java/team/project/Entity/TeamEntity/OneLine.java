package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.BaseEntity;
import team.project.Entity.Member;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
public class OneLine extends BaseEntity {

    @Id @GeneratedValue
    @Column(name = "one_line_id")
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String text;
    private LocalDate date;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected OneLine(){}

    public OneLine(String text, Team team, Member member) {
        this.text = text;
        this.team = team;
        this.member = member;
        this.date = LocalDate.now();
    }

    public void changeText(String text){
        this.text = text;
    }
}
