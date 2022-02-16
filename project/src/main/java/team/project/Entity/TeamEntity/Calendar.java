package team.project.Entity.TeamEntity;

import lombok.Getter;
import team.project.Entity.Member;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Calendar {

    @Id @GeneratedValue
    @Column(name = "calendar_id")
    private Long id;
    /**
     * groupId는 일반적으로 프로젝트를 기준으로 분류합니다
     * 만약 groupId가 0L 일 경우 전체일정을 뜻합니다.
     * */
    private Long groupId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String color;
    @Enumerated(EnumType.STRING)
    private DateSaveType dateType;
    @Column(columnDefinition = "TEXT")
    private String memo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public void changeGroupId(Long groupId){
        this.groupId = groupId;
    }

    public void changeCalendar(String title, String memo, LocalDateTime start, LocalDateTime end, DateSaveType dateType){
        this.title = title;
        startDate = start;
        endDate = end;
        this.memo = memo;
        this.dateType = dateType;
    }

    public void changeColor(String color){
        this.color = color;
    }
    protected Calendar(){}

    public Calendar(Team team, Member member) {
        this.team = team;
        this.member = member;
    }

    public static Calendar createSchedule(String title, String memo , String color, LocalDateTime start, LocalDateTime end, DateSaveType dateType, Long groupId, Member member , Team team){
        Calendar calendar = new Calendar(team , member);
        calendar.changeCalendar(title, memo, start, end, dateType);
        calendar.changeColor(color);
        calendar.changeGroupId(groupId);
        return calendar;
    }
}
