package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.project.CommonConst;
import team.project.Dto.Content.CreateContentDto;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.Calendar;
import team.project.Entity.TeamEntity.Content;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Project;
import team.project.Service.ContentService;
import team.project.Service.ProjectService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamProjectBoardController {

    private final ContentService contentService;
    private final ProjectService projectService;

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}")
    public String projectBoardList(Model model, @PathVariable("projectId") Long projectId, @PathVariable("teamId") Long teamId){
        List<BoardResponse> resultList = contentService.findProjectContent(projectId).stream().map(c -> {
            Member m = c.getMember();
            Project p = c.getProject();
            return new BoardResponse(
                    c.getId(), c.getTitle(), c.getText(), c.getCategory(),
                    m.getId(), m.getNickname(), m.getName(),
                    p.getId(), p.getTitle(),
                    c.getCreatedDate(), c.getModifiedDate());
        }).collect(Collectors.toList());
        model.addAttribute("contents", resultList);
        model.addAttribute("teamId", teamId);
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
    public String detailProjectBoardForm(Model model, @PathVariable("teamId") Long teamId){
        return "project/content/addProjectBoardForm";
    }

    @GetMapping("/teams/{teamId}/projects/bbs/{projectId}/{contentId}/edit")
    public String editProjectBoardForm(Model model, @PathVariable("contentId") Long contentId){
        Content content = contentService.findById(contentId);
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
    }
}
