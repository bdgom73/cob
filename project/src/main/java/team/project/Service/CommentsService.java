package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.Comments;
import team.project.Entity.TeamEntity.Content;
import team.project.Repository.CommentsRepository;
import team.project.Repository.ContentRepository;
import team.project.Repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final ContentRepository contentRepository;
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
    public void editComments(Long commentsId, String text){
        commentsRepository.findById(commentsId).ifPresent(c->{
            c.changeText(text);
        });
    }

    @Transactional
    public void editComments(Comments comment, String text){
        comment.changeText(text);
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
}
