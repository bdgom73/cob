package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team.project.CommonConst;
import team.project.Controller.Form.TeamForm.JoinMemberResponse;
import team.project.Controller.Form.TeamForm.TeamForm;
import team.project.Controller.Form.TeamForm.TeamsResponse;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Project;
import team.project.Entity.TeamEntity.Team;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.ContentService;
import team.project.Service.FreeContentService;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/@teams")
public class TeamManagerController {


    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final ContentService contentService;
    private final FreeContentService freeContentService;

    @GetMapping("/{teamId}")
    public String adminTeamPage(Model model, @SessionAttribute("UID") Long memberId, @PathVariable("teamId") Long teamId,@RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam){
        Team team = joinTeam.getTeam();
        Member member = team.getMember();
        TeamsResponse teamsResponse = new TeamsResponse(
                team.getId(),team.getName(),team.getIntroduction(),
                member.getId(),member.getNickname(), member.getName(), member.getEmail(),
                team.getCreatedDate(), team.getModifiedDate()
                );
        if(!team.getMember().getId().equals(memberId)){
            return "redirect:/teams/"+ teamId;
        }
        Map<String, Long> statDate = joinTeamService.findByStatDate(LocalDate.now(), teamId);
        model.addAttribute("teamForm", new TeamForm(team.getName(), team.getIntroduction()));
        model.addAttribute("team", teamsResponse);
        model.addAttribute("statDate", statDate);
        return "manager/managerTeam";
    }

    @PostMapping("/{teamId}/edit")
    public String editTeam(
            @Login Long memberId, @PathVariable("teamId") Long teamId,
            @Validated @ModelAttribute("team") TeamForm form, BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return "redirect:/@teams/"+teamId;
        }

        teamService.editTeam(teamId, form.getName(), form.getIntroduction(), memberId);
        return "redirect:/@teams/"+teamId;
    }

    @GetMapping("/{teamId}/user")
    public String userInTeam(
            Model model,
            @PathVariable("teamId") Long teamId,
            RedirectAttributes redirectAttributes,
            @RequestParam(value = "state", defaultValue = "OK") String state,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam
    ){
        state = state.toUpperCase();
        try{
            List<JoinMemberResponse> resultList;
            if(JoinState.valueOf(state) == JoinState.WAITING){
                resultList = joinTeamService.JoinTeamToResultList(teamId, JoinState.WAITING);
            }else if(JoinState.valueOf(state) == JoinState.BAN){
                if(!Objects.equals(joinTeam.getTeam().getMember().getId(), joinTeam.getMember().getId())){
                    throw new IllegalArgumentException();
                }
                resultList = joinTeamService.JoinTeamToResultList(teamId, JoinState.BAN);
            }else{
                resultList = joinTeamService.JoinTeamToResultList(teamId, JoinState.OK);
            }

            Map<String , Object> resultTeam = new HashMap<>();
            resultTeam.put("teamId", joinTeam.getTeam().getId());
            resultTeam.put("name", joinTeam.getTeam().getName());
            resultTeam.put("leaderId", joinTeam.getTeam().getMember().getId());
            model.addAttribute("joinTeams",resultList);
            model.addAttribute("team",resultTeam);
            return "manager/managerUser";
        }catch (IllegalStateException e){
            redirectAttributes.addFlashAttribute("resultMsg", "팀 접근 권한이 없습니다");
            return "redirect:/@teams/"+teamId;
        }catch (IllegalArgumentException e){
            return "redirect:/@teams/"+teamId+"/user";
        }

    }

    @GetMapping("/{teamId}/bbs")
    public String managementBBS(
            Model model,
            @PathVariable("teamId") Long teamId,
            RedirectAttributes redirectAttributes,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam,
            @RequestParam(value = "bbs", defaultValue = "free") String bbs,
            @RequestParam(value = "projectId", defaultValue = "0") Long projectId,
            @RequestParam(value = "category", defaultValue = "All") String category
    ){
        List<BoardResponse> resultList = new ArrayList<>();
        if(bbs.equals("free")){
            PageRequest pageRequest = PageRequest.of(0, 100, Sort.by("createdDate").descending());
            resultList = freeContentService.findMemberTeamByTeamId(teamId, pageRequest).stream().map(c -> {
                Member m = c.getMember();
                return new BoardResponse(
                        c.getId(), c.getTitle(), c.getText(),
                        m.getId(), m.getNickname(), m.getName(),
                        c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
            }).collect(Collectors.toList());
        }else if(bbs.equals("project")){
            if(!projectId.equals(0L)){
                resultList = contentService.findAllByProjectAndCategory(projectId, Progress.valueOf(category)).stream().map(c -> {
                    Member m = c.getMember();
                    return new BoardResponse(
                            c.getId(), c.getTitle(), c.getText(),
                            m.getId(), m.getNickname(), m.getName(),
                            c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
                }).collect(Collectors.toList());
            }
        }else{
//            return "redirect:/@teams/"+teamId + "/bbs?bbs=free";
        }

        model.addAttribute("teamId",teamId);
        model.addAttribute("boards", resultList);
        model.addAttribute("boardType", bbs);
        return "manager/managerBoard";
    }


    @Data
    @AllArgsConstructor
    class BoardResponse{
        private Long contentId;
        private String title;
        private String text;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
        private int count;
    }
}
