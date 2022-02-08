package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.CreateScheduleDto;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.Calendar;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Team;
import team.project.Repository.CalendarRepository;
import team.project.Repository.TeamRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final JoinTeamService joinTeamService;
    private final TeamRepository teamRepository;

    @Transactional
    public void createSchedule(Long teamId, Long memberId, CreateScheduleDto scheduleDto){
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, teamId);
        Calendar calendar = Calendar.createSchedule(
                scheduleDto.getTitle(), scheduleDto.getMemo() ,scheduleDto.getStartDate(), scheduleDto.getEndDate(),
                scheduleDto.getDateType(), scheduleDto.getGroupId(), joinTeam.getMember(), joinTeam.getTeam()
        );
        calendarRepository.save(calendar);
    }

    @Transactional
    public void createSchedule(Team team, Member member, CreateScheduleDto scheduleDto){
        Calendar calendar = Calendar.createSchedule(
                scheduleDto.getTitle(),scheduleDto.getMemo(), scheduleDto.getStartDate(), scheduleDto.getEndDate(),
                scheduleDto.getDateType(), scheduleDto.getGroupId(), member , team
        );
        calendarRepository.save(calendar);
    }

    public List<Calendar> monthSchedule(int year , int month, Long teamId){
        Optional<Team> findTeam = teamRepository.findById(teamId);
        Team team = findTeam.get();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year,month-1);
        int end = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, end);
        return calendarRepository.findAllByTeamAndStartDateBetween(team,startDate, endDate);
    }

    public List<Calendar> monthSchedule(int year , int month, Team team){
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(year,month-1);
        int end = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, end);
        return calendarRepository.findAllByTeamAndStartDateBetween(team,startDate, endDate);
    }

    public Calendar findSchedule(Long calendarId){
        return calendarRepository.findById(calendarId).orElseThrow(()->{
            throw new IllegalStateException("존재하지 않는 일정입니다");
        });
    }
}
