package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.Content.FreeCreateContentDto;
import team.project.Dto.Content.UpdateFreeContentDto;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.*;
import team.project.Repository.Team.FreeContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeContentService {

    private final FreeContentRepository contentRepository;
    private final ProjectService projectService;
    private final JoinTeamService joinTeamService;
    private final TeamService teamService;

    @Transactional
    public Long writeContent(Long memberId, FreeCreateContentDto contentDto){
        Team team = teamService.findById(contentDto.getTeamId());
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, team.getId());
        FreeContent content = createContent(contentDto, team, joinTeam.getMember());
        content.setNotice(contentDto.getNotice());
        contentRepository.save(content);
        return content.getId();
    }

    @Transactional
    public void updateContent(Long contentId, UpdateFreeContentDto contentDto){
        FreeContent content = findById(contentId);
        content.updateContent(contentDto.getTitle(), contentDto.getText());
        content.setNotice(contentDto.getNotice());
    }

    @Transactional
    public void updateContent(FreeContent content, UpdateFreeContentDto contentDto){
        content.updateContent(contentDto.getTitle(), contentDto.getText());
        content.setNotice(contentDto.getNotice());
    }

    public FreeContent findById(Long id){
        return contentRepository.findById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }
    public FreeContent findMemberById(Long id){
        return contentRepository.findMemberById(id).orElseThrow(()->{
            throw new IllegalStateException("해당 글이 존재하지 않습니다");
        });
    }

    public List<FreeContent> findMemberTeamByTeamId(Long teamId, Pageable pageable){
        return contentRepository.findMemberAndTeamByTeamId(teamId,pageable);
    }

    private FreeContent createContent(FreeCreateContentDto contentDto, Team team, Member member) {
        FreeContent content = new FreeContent(contentDto.getTitle(), contentDto.getText());
        content.setMember(member);
        content.setTeam(team);
        content.setNotice(content.getNotice());
        return content;
    }

}
