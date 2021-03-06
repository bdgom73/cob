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
import team.project.Repository.Team.CommentsRepository;
import team.project.Repository.Team.ContentRepository;
import team.project.Repository.MemberRepository;
import team.project.Repository.Team.FreeCommentsRepository;
import team.project.Repository.Team.FreeContentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final FreeCommentsRepository freeCommentsRepository;
    private final ContentRepository contentRepository;
    private final FreeContentRepository freeContentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void writeComments(String text, Content content, Member member){
        Comments comments = new Comments(text,content,member);
        commentsRepository.save(comments);
    }
    @Transactional
    public void writeComments(String text, Long contentId, Long memberId){
        Content content = contentRepository.findById(contentId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        Comments comments = new Comments(text,content,member);
        commentsRepository.save(comments);
    }

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
    public void editComments(Long commentsId, String text){
        commentsRepository.findById(commentsId).ifPresent(c->{
            c.changeText(text);
        });
    }

    @Transactional
    public void editComments(Comments comment, String text){
        comment.changeText(text);
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

    public List<Comments> findAllByContentId(Long contentId){
        return commentsRepository.findAllByContent(contentId);
    }

    public List<Comments> findAllByContentId(Long contentId, Pageable pageable){
        return commentsRepository.findAllByContent(contentId, pageable);
    }
    public Comments findFetchById(Long id){
        return commentsRepository.findFetchById(id).orElseThrow(()->{
            throw new IllegalStateException();
        });
    }

    public List<FreeComments> findAllByFreeContentId(Long contentId){
        return freeCommentsRepository.findAllByContent(contentId);
    }

    public List<FreeComments> findAllByFreeContentId(Long contentId, Pageable pageable){
        return freeCommentsRepository.findAllByContent(contentId, pageable);
    }
    public FreeComments findFreeCommentsFetchById(Long id){
        return freeCommentsRepository.findFetchById(id).orElseThrow(()->{
            throw new IllegalStateException();
        });
    }
}
