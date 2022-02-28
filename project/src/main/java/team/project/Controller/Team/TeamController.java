package team.project.Controller.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.QueryException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
import team.project.Repository.Team.OneLineRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final OneLineRepository oneLineRepository;

    @GetMapping("/teams")
    public String teams(Model model,
            @RequestParam(name = "page", defaultValue = "0") int page ,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "direction", defaultValue = "#{T(org.springframework.data.domain.Sort.Direction).DESC}") Sort.Direction direction,
            @RequestParam(value = "property", defaultValue = "createdDate") String property
    ){
        try{
            PageRequest pageRequest = PageRequest.of(page, size, direction, property);
            List<TeamsResponse> teams = teamService.findAllJoinMember(pageRequest)
                    .stream().map(TeamsResponse::new).collect(Collectors.toList());
            model.addAttribute("teams", teams);
            return "team/teamHome";
        }catch (IllegalArgumentException | QueryException | InvalidDataAccessApiUsageException e ){
            return "redirect:/teams?property=createdDate&direction=DESC";
        }

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

    @GetMapping("/team/create")
    public String teamForm(Model model, @Login Long memberId,RedirectAttributes attributes){
        if(memberId == null){
            attributes.addFlashAttribute("resultMsg","로그인 후 이용할 수 있는 서비스입니다.");
            return "redirect:/teams";
        }
        model.addAttribute("team", new TeamForm());
        return "team/addTeamForm";
    }

    @PostMapping("/team/create")
    public String createTeamForm(
            @Login Long memberId,
            @ModelAttribute("team") @Validated TeamForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes
    ){
        if(bindingResult.hasErrors()){
            log.info("bindingResult  {}", bindingResult);
            return "team/addTeamForm";
        }
        try{
            Long teamId = teamService.createTeam(form.getName(), form.getIntroduction(), memberId);
            redirectAttributes.addAttribute("teamId", teamId);
            return "redirect:/teams";
        }catch (IllegalStateException e){
            bindingResult.rejectValue("name", null, e.getMessage());
            return "team/addTeamForm";
        }catch (UsernameNotFoundException e){
            bindingResult.reject("usernameNotFound", e.getMessage());
            return "team/addTeamForm";
        }

    }

    @GetMapping("/teams/{teamId}/edit")
    public String editTeamForm(@Login Long memberId, @PathVariable("teamId") Long teamId, Model model){
        Team team = teamService.findById(teamId);
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
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam
    ){
        try{
            List<JoinMemberResponse> resultList = joinTeamService.JoinTeamToResultList(teamId, JoinState.OK);

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
            @SessionAttribute("UID") Long memberId,
            @RequestAttribute("checkJoinTeam") JoinTeam joinTeam,
            @RequestParam("state") String state,
            @RequestParam("memberId") Long joinMemberId,
            RedirectAttributes attributes
    ){
        if(joinTeam.getTeam().getMember().getId().equals(memberId)){
            String s = state.toUpperCase();
            if(s.equals("OK") || s.equals("WAITING") || s.equals("BAN")) {
                if(joinTeam.getTeam().getMember().getId().equals(joinMemberId)){
                    attributes.addFlashAttribute("resultMsg","팀장은 상태변경이 불가능합니다");
                    return "redirect:/teams/"+teamId+"/user";
                }
                teamService.changeState(teamId,joinMemberId,JoinState.valueOf(s));
            }else if(s.equals("REJECT")){
                if(joinTeam.getTeam().getMember().getId().equals(joinMemberId)){
                    attributes.addFlashAttribute("resultMsg","팀장은 상태변경이 불가능합니다");
                    return "redirect:/teams/"+teamId+"/user";
                }else{
                    teamService.doReject(teamId,joinMemberId);
                }

            }
        }
        return "redirect:/teams/"+teamId+"/user";
    }

    @PostMapping("/team/{teamId}/apply")
    @ResponseBody
    public void applyTeam(@PathVariable("teamId") Long teamId , @Login Long memberId, HttpServletResponse response) throws IOException {
        teamService.applyTeam(teamId,memberId);

    }

    @PostMapping("/teams/{teamId}/delete")
    public String deleteTeam(@PathVariable("teamId") Long teamId, RedirectAttributes redirectAttributes, @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam) {
        if(!joinTeam.getTeam().getMember().getId().equals(joinTeam.getMember().getId())){
            redirectAttributes.addFlashAttribute("resultMsg",  "삭제권한이 없습니다.");
            return "redirect:/teams/"+teamId;
        }
        teamService.deleteTeam(teamId);
        redirectAttributes.addFlashAttribute("resultMsg",  "팀을 삭제했습니다 (더이상 복구할 수 없습니다)");
        return "redirect:/teams";
    }
}
