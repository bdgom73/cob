package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.project.CommonConst;
import team.project.Dto.CreateScheduleDto;
import team.project.Entity.TeamEntity.Calendar;
import team.project.Entity.TeamEntity.DateSaveType;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.CalendarService;
import team.project.Service.JoinTeamService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamCalendarController {

    private final CalendarService calendarService;
    private final JoinTeamService joinTeamService;
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
            Boolean allDay;
            if (c.getDateType().equals(DateSaveType.MONTH)) {
                start = c.getStartDate().toString();
                end = c.getEndDate().toString();
            } else {
                start = c.getStartDate().toString();
                end = c.getEndDate().toString();

            }
            allDay = start.equals(end);
            if(start.contains("T00:00") && end.contains("T00:00")){
                allDay = true;
            }
            return new CalendarResponse(c.getId(), c.getGroupId(), c.getTitle(), start, end, c.getDateType(), allDay , c.getMemo(),
                    c.getMember().getId(), c.getMember().getNickname(), c.getMember().getName(),c.getColor());
        }).collect(Collectors.toList());

        model.addAttribute("calendars", resultList);

        return "projectCalendar/projectCalendar";
    }

    @GetMapping("/teams/{teamId}/calendar/create")
    public String createCalendarForm(Model model){
        model.addAttribute("calendar", new CalendarForm());
        return "projectCalendar/addProjectCalendarForm";
    }

    @PostMapping("/teams/{teamId}/calendar/create")
    public String postCreateCalendarForm(
            @PathVariable("teamId") Long teamId ,
            @ModelAttribute("calendar") CalendarForm form, BindingResult bindingResult,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam
    ){

        if(bindingResult.hasErrors()){
            log.info("bindingResult {}",bindingResult);
            return "redirect:/teams/"+teamId+"/calendar/create";
        }

        CreateScheduleDto createScheduleDto = new CreateScheduleDto(form.getGroupId(), form.getTitle(), form.getStart(), form.getEnd(),
                Objects.equals(form.getDateType(), "DAY") ? DateSaveType.DAY : DateSaveType.MONTH, form.getMemo(), form.getColor());
        calendarService.createSchedule(joinTeam.getTeam(), joinTeam.getMember(), createScheduleDto);

        return "redirect:/teams/"+teamId+"/calendar";
    }

    @GetMapping("/teams/{teamId}/calendar/{calendarId}/edit")
    public String editCalendarForm(Model model, @PathVariable("calendarId") Long calendarId, @PathVariable("teamId") Long teamId, @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam){
        try{
            Calendar calendar = calendarService.findSchedule(calendarId);

            if(!calendar.getMember().getId().equals(joinTeam.getMember().getId()) || !joinTeam.getTeam().getMember().getId().equals(joinTeam.getMember().getId())){
                throw new IllegalStateException();
            }

            CalendarForm form = new CalendarForm();
            form.setGroupId(calendar.getGroupId());
            form.setTitle(calendar.getTitle());
            form.setMemo(calendar.getMemo());
            form.setStart(calendar.getStartDate());
            form.setEnd(calendar.getEndDate());
            form.setDateType(calendar.getDateType().toString());
            model.addAttribute("calendar", form);
            return "projectCalendar/editProjectCalendarForm";
        }catch (IllegalStateException e){
            return "redirect:/teams/"+ teamId + "/calendar";
        }
    }

    @PostMapping("/teams/{teamId}/calendar/{calendarId}/edit")
    public String postEditCalendarForm(
            @PathVariable("teamId") Long teamId ,
            @PathVariable("calendarId") Long calendarId ,
            @ModelAttribute("calendar") CalendarForm form, BindingResult bindingResult
//            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam
    ){

        if(bindingResult.hasErrors()){
            log.info("bindingResult {}",bindingResult);
            return "redirect:/teams/"+teamId+"/calendar/"+calendarId+"/edit";
        }

        CreateScheduleDto createScheduleDto = new CreateScheduleDto(form.getGroupId(), form.getTitle(), form.getStart(), form.getEnd(),
                Objects.equals(form.getDateType(), "DAY") ? DateSaveType.DAY : DateSaveType.MONTH, form.getMemo(), form.getColor());

        calendarService.editSchedule(calendarId, createScheduleDto);
        return "redirect:/teams/"+teamId+"/calendar";
    }

    @GetMapping("/teams/{teamId}/calendar/{calendarId}")
    public String calendarDetail(@PathVariable("calendarId") Long calendarId, Model model){
        Calendar c = calendarService.findSchedule(calendarId);
        String start;
        String end;
        if (c.getDateType().equals(DateSaveType.MONTH)) {
            start = c.getStartDate().toLocalDate().toString();
            end = c.getEndDate().toLocalDate().toString();
        } else {
            start = c.getStartDate().toString();
            end = c.getEndDate().toString();
        }
        CalendarResponse calendarResponse = new CalendarResponse(c.getId(), c.getGroupId(), c.getTitle(), start, end, c.getDateType(),  c.getDateType().equals(DateSaveType.MONTH), c.getMemo(),
                c.getMember().getId(), c.getMember().getNickname(), c.getMember().getName(),c.getColor());
        model.addAttribute("calendar", calendarResponse);
        return "";
    }

    // API
    @GetMapping("/api/teams/{teamId}/calendar")
    @ResponseBody
    public Map<String, Object> calendarApi(
            @PathVariable("teamId") Long teamId, @SessionAttribute("UID") Long uid,
            @RequestParam(value = "start") LocalDateTime startDate, @RequestParam(value = "end") LocalDateTime endDate
    ){
        if(uid == null){
            throw new IllegalStateException("접근 불가능합니다");
        }

        List<CalendarResponse> resultList = calendarService.rangeSchedule(startDate, endDate, teamId).stream().map(c -> {
            String start;
            String end;
            Boolean allDay;
            if (c.getDateType().equals(DateSaveType.MONTH)) {
                start = c.getStartDate().toString();
                end = c.getEndDate().toString();
            } else {
                start = c.getStartDate().toString();
                end = c.getEndDate().toString();

            }
            allDay = start.equals(end);
            if(start.contains("T00:00") && end.contains("T00:00")){
                allDay = true;
            }
            return new CalendarResponse(c.getId(), c.getGroupId(), c.getTitle(), start, end, c.getDateType(), allDay , c.getMemo(),
                    c.getMember().getId(), c.getMember().getNickname(), c.getMember().getName(), c.getColor());

        }).collect(Collectors.toList());

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("data", resultList);
        return resultMap;
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
        private Boolean allDay;
        private String memo;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private String color;
    }

    @Data
    class CalendarForm{
        private Long groupId;
        private String title;
        private String memo;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime start;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
        private LocalDateTime end;
        private String dateType;
        private String color;
    }
}
