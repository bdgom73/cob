package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.Comments;
import team.project.Entity.TeamEntity.Content;
import team.project.Entity.TeamEntity.FreeComments;
import team.project.Entity.TeamEntity.FreeContent;
import team.project.Repository.MemberRepository;
import team.project.Repository.Team.CommentsRepository;
import team.project.Repository.Team.ContentRepository;
import team.project.Repository.Team.FreeCommentsRepository;
import team.project.Repository.Team.FreeContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FreeCommentsService {

    private final FreeCommentsRepository freeCommentsRepository;
    private final FreeContentRepository freeContentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void writeFreeComments(String text, Long freeContentId, Long memberId){
        FreeContent content = freeContentRepository.findById(freeContentId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        FreeComments comments = new FreeComments(text, content ,member);
        freeCommentsRepository.save(comments);
    }


    @Transactional
    public void editFreeComments(FreeComments comment, String text){
        comment.changeText(text);
    }

    @Transactional
    public void editFreeComments(Long commentsId, String text){
        freeCommentsRepository.findById(commentsId).ifPresent(c->{
            c.changeText(text);
        });
    }


    public List<FreeComments> findAllByContentId(Long contentId){
        return freeCommentsRepository.findAllByContent(contentId);
    }

    public List<FreeComments> findAllByContentId(Long contentId, Pageable pageable){
        return freeCommentsRepository.findAllByContent(contentId, pageable);
    }
    public FreeComments findFetchById(Long id){
        return freeCommentsRepository.findFetchById(id).orElseThrow(()->{
            throw new IllegalStateException();
        });
    }
}
