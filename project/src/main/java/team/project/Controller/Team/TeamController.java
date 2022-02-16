package team.project.Controller.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
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
import team.project.Entity.*;
import team.project.Entity.TeamEntity.Team;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Repository.JoinTeamRepository;
import team.project.Repository.TeamRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import javax.persistence.Convert;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final JoinTeamRepository joinTeamRepository;
    private final TeamRepository teamRepository;

    @GetMapping("/teams")
    public String teams(Model model,
            @RequestParam(name = "page", defaultValue = "0") int page ,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
        List<TeamsResponse> teams = teamService.findAllJoinMember(PageRequest.of(page, size))
                .stream().map(TeamsResponse::new).collect(Collectors.toList());
        model.addAttribute("teams", teams);
        return "team/teamHome";
    }

    @GetMapping("/teams/{teamId}")
    public String teamView(Model model,@RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam){
        Team team = joinTeam.getTeam();
        Member member = team.getMember();
        TeamsResponse teamsResponse = new TeamsResponse(
                team.getId(),team.getName(),team.getIntroduction(),
                member.getId(), member.getNickname(), member.getName(),member.getEmail(),
                team.getCreatedDate(), team.getModifiedDate()
        );
        model.addAttribute("team", teamsResponse);
        model.addAttribute("nlString", System.getProperty("line.separator"));
        return "team/fragment/teamMain";
    }

    @GetMapping("/teams/create")
    public String teamForm(Model model, @Login Long memberId){
        model.addAttribute("team", new TeamForm());
        return "team/addTeamForm";
    }

    @PostMapping("/teams/create")
    public String createTeamForm(
            @Login Long memberId,
            @Validated @ModelAttribute("team") TeamForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes
    ){
        if(bindingResult.hasErrors()){
            return "team/addTeamForm";
        }
        Long teamId = teamService.createTeam(form.getName(), form.getIntroduction(), memberId);
        redirectAttributes.addAttribute("teamId", teamId);
        return "redirect:/teams";
    }

    @GetMapping("/teams/{teamId}/edit")
    public String editTeamForm(@Login Long memberId, @PathVariable("teamId") Long teamId, Model model){
        Team team = teamService.findById(teamId);
//        JoinTeam joinTeam = joinTeamService.findMemberByMemberInTeam(memberId, teamId);
        if(!team.getMember().getId().equals(memberId)){
            return "redirect:/teams/"+ teamId;
        }
        model.addAttribute("team", new TeamForm(team.getName(), team.getIntroduction()));
        model.addAttribute("teamId", team.getId());
        return "team/fragment/editTeamForm";
    }

    @PostMapping("/teams/{teamId}/edit")
    public String editTeam(
            @Login Long memberId, @PathVariable("teamId") Long teamId,
            @Validated @ModelAttribute("team") TeamForm form, BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return "redirect:/teams";
        }

        teamService.editTeam(teamId, form.getName(), form.getIntroduction(), memberId);
        return "redirect:/teams/"+teamId;
    }


    @GetMapping("/teams/{teamId}/user")
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
            return "team/fragment/teamUser";
        }catch (IllegalStateException e){
            redirectAttributes.addFlashAttribute("resultMsg", "팀 접근 권한이 없습니다");
            return "redirect:/teams";
        }catch (IllegalArgumentException e){
            return "redirect:/teams/"+teamId+"/user";
        }

    }

    @PostMapping("/teams/{teamId}/state")
    public String changeState(
            @PathVariable("teamId") Long teamId,
            @Login Long memberId,
            @RequestAttribute("checkJoinTeam") JoinTeam joinTeam,
            @RequestParam("state") String state,
            @RequestParam("memberId") Long joinMemberId
    ){
        if(joinTeam.getTeam().getMember().getId().equals(memberId)){
            String s = state.toUpperCase();
            if(s.equals("OK") || s.equals("WAITING") || s.equals("BAN")) {
                teamService.changeState(teamId,joinMemberId,JoinState.valueOf(s));
            }else if(s.equals("REJECT")){
                teamService.doReject(teamId,joinMemberId);
            }
        }
        return "redirect:/teams/"+teamId+"/user";
    }



    @PostMapping("/team/{teamId}/apply")
    @ResponseBody
    public void applyTeam(@PathVariable("teamId") Long teamId , @Login Long memberId, HttpServletResponse response) throws IOException {
        teamService.applyTeam(teamId,memberId);

    }
}
