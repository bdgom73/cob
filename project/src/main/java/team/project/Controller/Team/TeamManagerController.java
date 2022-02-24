package team.project.Controller.Team;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Team;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/@teams")
public class TeamManagerController {


    private final TeamService teamService;
    private final JoinTeamService joinTeamService;

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
        model.addAttribute("team", new TeamForm(team.getName(), team.getIntroduction()));
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
}
