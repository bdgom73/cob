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
import team.project.CommonConst;
import team.project.Dto.Content.CreateContentDto;
import team.project.Dto.Content.WriteCommentContentDto;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.*;
import team.project.Service.CommentsService;
import team.project.Service.ContentService;
import team.project.Service.ProjectService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamProjectBoardController {

    private final ContentService contentService;
    private final ProjectService projectService;
    private final CommentsService commentsService;

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}")
    public String projectBoardList(
            Model model, @PathVariable("projectId") Long projectId, @PathVariable("teamId") Long teamId,
            @RequestParam(value = "page",defaultValue = "0") int page, @RequestParam(value = "size",defaultValue = "20") int size,
            @RequestParam(value = "category",defaultValue = "All") String category , @RequestParam(value = "orderValue",defaultValue = "DESC") String orderValue
    ){
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdDate").descending());
        List<Content> contentList ;

        if(Objects.equals(category, "Requirements_Analysis")){
            contentList = contentService.findOrderProjectContent(projectId,Progress.Requirements_Analysis,pageRequest);
        }else if(Objects.equals(category, "Design")){
            contentList = contentService.findOrderProjectContent(projectId,Progress.Design,pageRequest);
        }else if(Objects.equals(category, "Programming")){
            contentList = contentService.findOrderProjectContent(projectId,Progress.Programming,pageRequest);
        }else if(Objects.equals(category, "Testing")){
            contentList = contentService.findOrderProjectContent(projectId,Progress.Testing,pageRequest);
        }else if(Objects.equals(category, "Maintenance")){
            contentList = contentService.findOrderProjectContent(projectId,Progress.Maintenance,pageRequest);
        }else{
            contentList = contentService.findProjectContent(projectId,pageRequest);
        }

        List<BoardResponse> resultList = contentList.stream().map(c -> {
            Member m = c.getMember();
            Project p = c.getProject();
            return new BoardResponse(
                    c.getId(), c.getTitle(), c.getText(), c.getCategory(),
                    m.getId(), m.getNickname(), m.getName(),
                    p.getId(), p.getTitle(),
                    c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
        }).collect(Collectors.toList());

        model.addAttribute("contents", resultList);
        model.addAttribute("teamId", teamId);
        model.addAttribute("projectId", projectId);
        return "project/content/projectBoard";
    }

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}/create")
    public String projectBoardForm(Model model, @PathVariable("teamId") Long teamId){
        model.addAttribute("projectBoard", new CreateBoardForm());
        model.addAttribute("teamId", teamId);
        return "project/content/addProjectBoardForm";
    }

    @PostMapping("/teams/{teamId}/projects/bbs/{projectId}/create")
    public String postProjectBoardForm(@PathVariable("teamId") Long teamId, @PathVariable("projectId") Long projectId, @SessionAttribute("UID") Long memberId,
        @ModelAttribute("projectBoard") CreateBoardForm form, BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult);
            return "redirect:/teams/"+teamId+"/projects/bbs/create";
        }

        if(memberId == null){
            return "redirect:/teams/"+teamId+"/projects/bbs/create";
        }

        try{
            Project project = projectService.findById(projectId);
            Long contentId = contentService.writeContent(memberId, new CreateContentDto(form.getTitle(), form.getText(), project.getId(), Progress.valueOf(form.getProgress())));
            return "redirect:/teams/"+teamId+"/projects/bbs/"+projectId + "/" + contentId;
        }catch (IllegalStateException e){
            return "redirect:/teams/"+teamId+"/projects/bbs/create";
        }

    }

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}/{contentId}")
    public String detailProjectBoardForm(Model model, @PathVariable("contentId") Long contentId){
        Content c = contentService.findMemberById(contentId);
        Member m = c.getMember();
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("createdDate").descending());
        List<CommentsResponse> commentsList = commentsService.findAllByContentId(contentId,pageRequest).stream().map(cm -> {
            Member m2 = cm.getMember();
            return new CommentsResponse(cm.getId(), cm.getText(),
                    m2.getId(), m2.getNickname(), m2.getName(),
                    cm.getCreatedDate(), cm.getModifiedDate());
        }).collect(Collectors.toList());
        BoardDetailResponse boardDetailResponse = new BoardDetailResponse(c.getId(), c.getTitle(), c.getText(), c.getCategory(),
                m.getId(), m.getNickname(), m.getName(), c.getCreatedDate(), c.getModifiedDate());

        model.addAttribute("content", boardDetailResponse);
        model.addAttribute("comments", commentsList);
        model.addAttribute("projectId", c.getProject().getId());
        model.addAttribute("commentForm", new WriteCommentContentDto());
        return "project/content/projectBoardDetail";
    }

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}/{contentId}/edit")
    public String editProjectBoardForm(Model model,
           @SessionAttribute("UID") Long memberId,
           @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam,
           @PathVariable("teamId") Long teamId,
           @PathVariable("projectId") Long projectId,
           @PathVariable("contentId") Long contentId
    ){

        Content content = contentService.findById(contentId);
        if(!joinTeam.getTeam().getMember().getId().equals(memberId)){
            if(!content.getMember().getId().equals(memberId)){
                return "redirect:/teams/"+ teamId +"/projects/bbs/" +projectId + "/" + contentId;
            }
        }

        CreateBoardForm createBoardForm = new CreateBoardForm();
        createBoardForm.setProjectId(content.getProject().getId());
        createBoardForm.setProgress(content.getCategory().toString());
        createBoardForm.setText(content.getText());
        createBoardForm.setTitle(content.getTitle());
        model.addAttribute("projectBoard", createBoardForm);
        return "project/content/editProjectBoardForm";
    }



    @Data
    class CreateBoardForm{
        private Long projectId;
        private String progress;
        private String title;
        private String text;
    }

    @Data
    @AllArgsConstructor
    class BoardResponse{
        private Long contentId;
        private String title;
        private String text;
        private Progress category;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private Long projectId;
        private String projectTitle;
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
        private Progress category;
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
