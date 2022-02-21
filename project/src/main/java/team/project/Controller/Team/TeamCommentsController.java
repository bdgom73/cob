package team.project.Controller.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import team.project.Dto.Content.EditCommentsRequest;
import team.project.Dto.Content.WriteCommentContentDto;
import team.project.Entity.TeamEntity.Comments;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.CommentsService;
import team.project.Service.JoinTeamService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TeamCommentsController {

    private final CommentsService commentsService;
    private final JoinTeamService joinTeamService;

    @PostMapping("/teams/{teamId}/projects/bbs/{projectId}/{contentId}/comments")
    public String writeComment(
            @ModelAttribute("commentForm") WriteCommentContentDto form, BindingResult bindingResult, @SessionAttribute("UID") Long memberId,
            @PathVariable("contentId") Long contentId, @PathVariable("teamId") Long teamId, @PathVariable("projectId") Long projectId){

        if(bindingResult.hasErrors()){
            return "redirect:/teams/"+ teamId + "/projects/bbs/"+projectId+"/"+contentId;
        }
        commentsService.writeComments(form.getContent(), contentId, memberId);
        return "redirect:/teams/"+ teamId + "/projects/bbs/"+projectId+ "/" +contentId;
    }

    @PostMapping("/api/teams/{teamId}/comments/{commentsId}/edit")
    @ResponseBody
    public ResponseEntity<EditCommentsRequest> apiEditComment(
            @Login Long memberId, HttpServletResponse response, @PathVariable("teamId") Long teamId,
            @RequestBody EditCommentsRequest commentsRequest, @PathVariable("commentsId") Long commentsId) throws IOException {

        if(memberId == null){
            response.sendError(HttpStatus.BAD_REQUEST.value(), "권한이 없습니다");
        }

        Comments comments = commentsService.findFetchById(commentsId);
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, teamId);

        if(!joinTeam.getTeam().getMember().getId().equals(memberId)){
            if(!comments.getMember().getId().equals(memberId)){
                response.sendError(HttpStatus.BAD_REQUEST.value(), "권한이 없습니다");
            }
        }

        commentsService.editComments(comments ,commentsRequest.getText());
        EditCommentsRequest editCommentsRequest = new EditCommentsRequest(commentsRequest.getText());
        return new ResponseEntity<>(editCommentsRequest, HttpStatus.OK);
    }

}
