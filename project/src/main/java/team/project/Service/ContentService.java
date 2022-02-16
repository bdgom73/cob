package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.Content.CreateContentDto;
import team.project.Dto.Content.UpdateContentDto;
import team.project.Entity.*;
import team.project.Entity.TeamEntity.Content;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Project;
import team.project.Entity.TeamEntity.Team;
import team.project.Repository.ContentRepository;
import team.project.Repository.ProjectCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final ProjectService projectService;
    private final JoinTeamService joinTeamService;
    private final ProjectCategoryRepository projectCategoryRepository;
    private final TeamService teamService;

    @Transactional
    public Long writeContent(Long memberId, CreateContentDto contentDto){
        Project project = projectService.findTeamById(contentDto.getProjectId());
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, project.getTeam().getId());
        Content content = createContent(contentDto, project, joinTeam.getMember());

        contentRepository.save(content);

        return content.getId();
    }

    @Transactional
    public void updateContent(Long contentId, UpdateContentDto contentDto){
        Content content = findById(contentId);
        content.updateContent(contentDto.getTitle(), contentDto.getText());
    }


    @Transactional
    public ProjectCategory createCategory(Long teamId, String categoryName){
        Team team = teamService.findById(teamId);
        ProjectCategory projectCategory = new ProjectCategory(categoryName);
        projectCategory.setTeam(team);
        projectCategoryRepository.save(projectCategory);
        return projectCategory;
    }

    public Content findById(Long id){
        return contentRepository.findById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }

    public List<Content> findProjectContent(Long projectId){
        PageRequest pageRequest = PageRequest.of(0, 100);
        return contentRepository.findByProjectId(projectId,pageRequest);
    }
    public List<Content>  findProjectContent(Long projectId, Pageable pageable){
        return contentRepository.findByProjectId(projectId, pageable);
    }


    @Transactional
    public void deleteCategory(Long categoryId){
        projectCategoryRepository.deleteById(categoryId);
    }

    private Content createContent(CreateContentDto contentDto, Project project, Member member) {
        Content content = new Content(contentDto.getTitle(), contentDto.getText());
        content.setMember(member);
        content.setProject(project);
        content.setCategory(contentDto.getCategory());
        return content;
    }

}
