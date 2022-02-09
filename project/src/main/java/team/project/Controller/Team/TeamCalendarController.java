package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import team.project.CommonConst;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.DateSaveType;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Team;
import team.project.Service.CalendarService;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamCalendarController {

    private final CalendarService calendarService;

    @GetMapping("/teams/{teamId}/calendar")
    public String calendar(
            @RequestParam(value = "year", required = false) Integer year, @RequestParam(value = "month",required = false) Integer month,  Model model,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam

    ){
        if(year == null)
            year = LocalDate.now().getYear();
        if(month == null)
            month = LocalDate.now().getMonthValue();

        List<CalendarResponse> resultList = calendarService.monthSchedule(year, month, joinTeam.getTeam()).stream().map(c -> {
            String start;
            String end;
            if (c.getDateType().equals(DateSaveType.MONTH)) {
                start = c.getStartDate().toLocalDate().toString();
                end = c.getEndDate().toLocalDate().toString();
            } else {
                start = c.getStartDate().toString();
                end = c.getEndDate().toString();
            }
            return new CalendarResponse(c.getId(), c.getGroupId(), c.getTitle(), start, end, c.getDateType(), c.getMemo(),
                    c.getMember().getId(), c.getMember().getNickname(), c.getMember().getName());
        }).collect(Collectors.toList());

        model.addAttribute("calendars", resultList);

        return "projectCalendar/projectCalendar";
    }

    @GetMapping("/teams/{teamId}/calendar/create")
    public String createCalendarForm(Model model){
        model.addAttribute("calendar", new CalendarForm());
        return "projectCalendar/addProjectCalendarForm";
    }

    @Data
    @AllArgsConstructor
    class CalendarResponse{
        private Long calendarId;
        private Long groupId;
        private String title;
        private String start;
        private String end;
        private DateSaveType dateType;
        private String memo;
        private Long memberId;
        private String memberNickname;
        private String memberName;
    }

    @Data
    class CalendarForm{
        private Long groupId;
        private String title;
        private String memo;
        private String start;
        private String end;
        private DateSaveType dateType;
    }
}
