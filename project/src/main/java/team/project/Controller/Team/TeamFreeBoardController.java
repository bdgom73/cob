package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team.project.CommonConst;
import team.project.Dto.Content.CreateContentDto;
import team.project.Dto.Content.FreeCreateContentDto;
import team.project.Dto.Content.UpdateFreeContentDto;
import team.project.Dto.Content.WriteCommentContentDto;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.*;
import team.project.Repository.Team.FreeContentRepository;
import team.project.Service.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamFreeBoardController {

    private final FreeContentService contentService;
    private final ProjectService projectService;
    private final FreeCommentsService commentsService;
    private final FreeContentRepository freeContentRepository;

    @GetMapping("/teams/{teamId}/bbs/free")
    public String projectBoardList(
            Model model, @PathVariable("teamId") Long teamId,
            @RequestParam(value = "page",defaultValue = "0") int page, @RequestParam(value = "size",defaultValue = "20") int size
    ){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<FreeContent> contentList = contentService.findMemberTeamByTeamId(teamId, pageRequest);

        List<BoardResponse> resultList = contentList.stream().map(c -> {
            Member m = c.getMember();
            return new BoardResponse(
                    c.getId(), c.getTitle(), c.getText(), c.getNotice(),
                    m.getId(), m.getNickname(), m.getName(),
                    c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
        }).collect(Collectors.toList());

        model.addAttribute("contents", resultList);
        model.addAttribute("teamId", teamId);
        return "project/freeContent/projectBoard";
    }

    @GetMapping("/teams/{teamId}/bbs/free/create")
    public String projectBoardForm(Model model, @PathVariable("teamId") Long teamId){
        model.addAttribute("projectBoard", new CreateBoardForm());
        model.addAttribute("teamId", teamId);
        return "project/freeContent/addProjectBoardForm";
    }

    @PostMapping("/teams/{teamId}/bbs/free/create")
    public String postProjectBoardForm(@PathVariable("teamId") Long teamId,  @SessionAttribute("UID") Long memberId,
        @ModelAttribute("projectBoard") CreateBoardForm form, BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult);
            return "redirect:/teams/"+teamId+"/bbs/free/create";
        }

        if(memberId == null){
            return "redirect:/teams/"+teamId+"/bbs/free/create";
        }
        log.info("Create value = {}", form);
        try{
            Long contentId = contentService.writeContent(memberId, new FreeCreateContentDto(form.getTitle(), form.getText(), form.getTeamId(), form.getNotice()));
            return "redirect:/teams/"+teamId+"/bbs/free/" + contentId;
        }catch (IllegalStateException e){
            return "redirect:/teams/"+teamId+"/bbs/free/create";
        }

    }

    @GetMapping("/teams/{teamId}/bbs/free/{contentId}")
    public String detailProjectBoardForm(Model model, @PathVariable("contentId") Long contentId){
        FreeContent c = contentService.findMemberById(contentId);
        Member m = c.getMember();
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("createdDate").descending());
        List<CommentsResponse> commentsList = commentsService.findAllByContentId(contentId,pageRequest).stream().map(cm -> {
            Member m2 = cm.getMember();
            return new CommentsResponse(cm.getId(), cm.getText(),
                    m2.getId(), m2.getNickname(), m2.getName(),
                    cm.getCreatedDate(), cm.getModifiedDate());
        }).collect(Collectors.toList());

        BoardDetailResponse boardDetailResponse = new BoardDetailResponse(c.getId(), c.getTitle(), c.getText(), c.getNotice(),
                m.getId(), m.getNickname(), m.getName(), c.getCreatedDate(), c.getModifiedDate());

        model.addAttribute("content", boardDetailResponse);
        model.addAttribute("comments", commentsList);
        model.addAttribute("commentForm", new WriteCommentContentDto());
        return "project/freeContent/projectBoardDetail";
    }

    @GetMapping("/teams/{teamId}/bbs/free/{contentId}/edit")
    public String editProjectBoardForm(Model model,
           @SessionAttribute("UID") Long memberId,
           @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam,
           @PathVariable("teamId") Long teamId,
           @PathVariable("contentId") Long contentId
    ){

        FreeContent content = contentService.findById(contentId);
        if(!joinTeam.getTeam().getMember().getId().equals(memberId)){
            if(!content.getMember().getId().equals(memberId)){
                return "redirect:/teams/"+ teamId +"/bbs/free/" + contentId;
            }
        }

        CreateBoardForm createBoardForm = new CreateBoardForm();
        createBoardForm.setTeamId(content.getTeam().getId());
        createBoardForm.setNotice(content.getNotice());
        createBoardForm.setText(content.getText());
        createBoardForm.setTitle(content.getTitle());
        model.addAttribute("board", createBoardForm);
        return "project/freeContent/editProjectBoardForm";
    }

    @PostMapping("/teams/{teamId}/bbs/free/{contentId}/edit")
    public String editPostProjectBoardForm(
            @ModelAttribute("board") CreateBoardForm form, BindingResult bindingResult,
           @SessionAttribute("UID") Long memberId,
           @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam,
           @PathVariable("teamId") Long teamId,
           @PathVariable("contentId") Long contentId
    ){

        if(bindingResult.hasErrors()){
            return "redirect:/teams/"+teamId+"/bbs/free/"+contentId;
        }
        FreeContent content = contentService.findById(contentId);
        if(!joinTeam.getTeam().getMember().getId().equals(memberId)){
            if(!content.getMember().getId().equals(memberId)){
                return "redirect:/teams/"+ teamId +"/bbs/free/" + contentId;
            }
        }
        UpdateFreeContentDto updateFreeContentDto = new UpdateFreeContentDto(form.getTitle(), form.getText(), form.getNotice());
        contentService.updateContent(content, updateFreeContentDto);
        return "redirect:/teams/"+ teamId +"/bbs/free/" + contentId;
    }

    @PostMapping("/teams/{teamId}/bbs/free/{contentId}/delete")
    public String deleteProjectBoardForm(
            @SessionAttribute("UID") Long memberId,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam,
            @PathVariable("teamId") Long teamId,
            @PathVariable("contentId") Long contentId,
            RedirectAttributes attributes
    ){
        try{
            FreeContent content = contentService.findMemberById(contentId);
            if(content.getMember().getId().equals(memberId) || joinTeam.getTeam().getMember().getId().equals(memberId)){
                freeContentRepository.delete(content);
                attributes.addFlashAttribute("resultMsg","해당 게시글을 삭제했습니다");
            }else{
                throw new IllegalStateException("삭제 권한이 없습니다");
            }
            return "redirect:/teams/"+ teamId +"/bbs/free";
        }catch (Exception e){
            attributes.addFlashAttribute("resultMsg",e.getMessage());
            return "redirect:/teams/"+ teamId +"/bbs/free";
        }
    }

    @Data
    class CreateBoardForm{
        private Long teamId;
        private Boolean notice;
        private String title;
        private String text;
    }

    @Data
    @AllArgsConstructor
    class BoardResponse{
        private Long contentId;
        private String title;
        private String text;
        private Boolean notice;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
        private int count;
    }

    @Data
    @AllArgsConstructor
    class BoardDetailResponse{
        private Long contentId;
        private String title;
        private String text;
        private Boolean notice;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
    }

    @Data
    @AllArgsConstructor
    class CommentsResponse{
        private Long commentsId;
        private String text;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
    }
}
