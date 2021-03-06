package team.project.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.CreateProjectDto;
import team.project.Entity.*;
import team.project.Entity.TeamEntity.*;
import team.project.Repository.Team.CalendarRepository;
import team.project.Repository.Team.ContentRepository;
import team.project.Repository.ProjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final CalendarRepository calendarRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public Long createProject(Long leaderMemberId, CreateProjectDto createProjectDto){
        Team team = teamService.findById(createProjectDto.getTeamId());
        JoinTeam joinTeam = joinTeamService.findMemberByMemberInTeam(leaderMemberId, team.getId());

        if(!joinTeam.getJoinState().equals(JoinState.OK)){
            throw new IllegalStateException("해당 팀에서 프로젝트 기획 권한이 없습니다");
        }

        Member member = joinTeam.getMember();
        Project project = Project.create(
                createProjectDto.getTitle(), createProjectDto.getIntroduction(),
                createProjectDto.getStartDate(), createProjectDto.getEndDate(),
                team, member);

        projectRepository.save(project);
        return project.getId();
    }

    @Transactional
    public void changeProgress(Long projectId, Progress progress){
        Project project = findById(projectId);
        project.setProgress(progress);
    }
    @Transactional
    public void changeProgress(Project project, Progress progress){
        project.setProgress(progress);
    }

    @Transactional
    public Project changeProject(Long projectId , String title, String introduction, LocalDateTime startDate, LocalDateTime endDate){
        Project project = findMemberById(projectId);
        project.changeProject(title,introduction,startDate,endDate);
        return project;
    }

    @Transactional
    public Project changeProject(Project project , String title, String introduction, LocalDateTime startDate, LocalDateTime endDate){
        project.changeProject(title,introduction,startDate,endDate);
        return project;
    }


    @Transactional
    public void deleteProject(Long projectId){
        List<Calendar> calendars = calendarRepository.findByGroupId(projectId);
        List<Content> contents = contentRepository.findByProjectId(projectId);
        calendarRepository.deleteAll(calendars);
        contentRepository.deleteAll(contents);
        projectRepository.deleteById(projectId);
    }

    public Project findById(Long id){
        return projectRepository.findById(id).orElseThrow(()->{
            throw new IllegalStateException("존재하지 않는 프로젝트입니다");
        });
    }

    public Project findTeamById(Long id){
        return projectRepository.findTeamById(id).orElseThrow(()->{
            throw new IllegalStateException("존재하지 않는 프로젝트입니다");
        });
    }

    public Project findMemberById(Long id){
        return projectRepository.findMemberById(id).orElseThrow(()->{
            throw new IllegalStateException("존재하지 않는 프로젝트입니다");
        });
    }

    public Project findTeamAndMemberById(Long id){
        return projectRepository.findTeamAndMemberById(id).orElseThrow(()->{
            throw new IllegalStateException("존재하지 않는 프로젝트입니다");
        });
    }

    public List<Project> findAll(){
        return projectRepository.findAll();
    }

    public List<Project> findAllByTeam(Long teamId){
        return projectRepository.findAllByTeam(teamId);
    }

}
