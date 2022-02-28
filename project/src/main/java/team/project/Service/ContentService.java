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
import team.project.Repository.Team.ContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;
    private final ProjectService projectService;
    private final JoinTeamService joinTeamService;

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


    public Content findById(Long id){
        return contentRepository.findById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }
    public Content findMemberById(Long id){
        return contentRepository.findMemberById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }
    public Content findFetchMemberAndProjectById(Long id){
        return contentRepository.findFetchMemberAndProjectById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }
    public List<Content> findProjectContent(Long projectId){
        PageRequest pageRequest = PageRequest.of(0, 100);
        return contentRepository.findFetchByProjectId(projectId,pageRequest);
    }
    public List<Content>  findProjectContent(Long projectId, Pageable pageable){
        return contentRepository.findFetchByProjectId(projectId, pageable);
    }

    public List<Content>  findOrderProjectContent(Long projectId, Progress order,Pageable pageable){
        return contentRepository.findOrderByProjectId(projectId, order, pageable);
    }

    public List<Content> findAllByProjectAndCategory(Long projectId, Progress progress){
        if(progress.equals(Progress.All)){
            return contentRepository.findByProjectId(projectId);
        }else{
            return contentRepository.findAllByCategory(projectId, progress);
        }

    }

    private Content createContent(CreateContentDto contentDto, Project project, Member member) {
        Content content = new Content(contentDto.getTitle(), contentDto.getText());
        content.setMember(member);
        content.setProject(project);
        content.setCategory(contentDto.getCategory());
        return content;
    }

}
