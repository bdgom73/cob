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
import team.project.Entity.TeamEntity.FreeComments;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Repository.Team.CommentsRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.CommentsService;
import team.project.Service.JoinTeamService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TeamCommentsController {

    private final CommentsService commentsService;
    private final CommentsRepository commentsRepository;
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
            response.sendError(HttpStatus.BAD_REQUEST.value(), "????????? ????????????");
        }

        Comments comments = commentsService.findFetchById(commentsId);
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, teamId);

        if(!joinTeam.getTeam().getMember().getId().equals(memberId)){
            if(!comments.getMember().getId().equals(memberId)){
                response.sendError(HttpStatus.BAD_REQUEST.value(), "????????? ????????????");
            }
        }

        commentsService.editComments(comments ,commentsRequest.getText());
        EditCommentsRequest editCommentsRequest = new EditCommentsRequest(commentsRequest.getText());
        return new ResponseEntity<>(editCommentsRequest, HttpStatus.OK);
    }

    @PostMapping("/teams/{teamId}/projects/bbs/free/{contentId}/comments")
    public String writeComment(
            @ModelAttribute("commentForm") WriteCommentContentDto form, BindingResult bindingResult, @SessionAttribute("UID") Long memberId,
            @PathVariable("contentId") Long contentId, @PathVariable("teamId") Long teamId){

        if(bindingResult.hasErrors()){
            return "redirect:/teams/"+ teamId + "/projects/bbs/free/"+contentId;
        }
        commentsService.writeComments(form.getContent(), contentId, memberId);
        return "redirect:/teams/"+ teamId + "/projects/bbs/" +contentId;
    }

    @PostMapping("/api/teams/{teamId}/projects/comments/{commentsId}/delete")
    @ResponseBody
    public ResponseEntity deleteComment(
            @SessionAttribute("UID") Long memberId,
            @PathVariable("teamId") Long teamId,
            @PathVariable("commentsId") Long commentsId
    ){
        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(memberId, teamId);
        Comments comments = commentsService.findFetchById(commentsId);
        Map<String,Object> resultMap = new HashMap<>();
        if( joinTeam.getTeam().getMember().getId().equals(memberId) || comments.getMember().getId().equals(memberId) ){
            commentsRepository.delete(comments);
            resultMap.put("message", "????????? ??????????????????");
            resultMap.put("status", HttpStatus.OK);
            return new ResponseEntity<>(resultMap,HttpStatus.OK);
        } else {
            resultMap.put("message", "????????? ????????? ????????????");
            resultMap.put("status", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(resultMap,HttpStatus.BAD_REQUEST);
        }
    }


}
