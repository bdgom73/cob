package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.CreateProjectDto;
import team.project.Entity.*;
import team.project.Repository.ProjectMemberRepository;
import team.project.Repository.ProjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final ProjectMemberRepository projectMemberRepository;

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
    public void addProjectMember(Long teamId ,Long projectId, Long... memberIds){
        Project project = findById(projectId);

        for (Long memberId : memberIds) {
            JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
            if(!joinTeam.getJoinState().equals(JoinState.OK)){
                throw new IllegalStateException("프로젝트 참여할 수 없습니다");
            }
            ProjectMember.createProject("참여자", joinTeam.getMember(), project);
        }
    }

    @Transactional
    public void projectMemberRoleChange(Long projectId, Long memberId, String role){
        ProjectMember projectMember = projectMemberRepository.findByProjectIdAndMemberId(projectId, memberId).orElseThrow(() -> {
            throw new IllegalStateException("해당 프로젝트 인원이 아닙니다");
        });
        projectMember.changeRole(role);
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

    public List<Project> findAll(){
        return projectRepository.findAll();
    }

    public List<Project> findAllByTeam(Long teamId){
        return projectRepository.findAllByTeam(teamId);
    }

}
