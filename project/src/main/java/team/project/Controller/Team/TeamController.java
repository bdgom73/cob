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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team.project.Entity.*;
import team.project.Repository.JoinTeamRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final JoinTeamRepository joinTeamRepository;

    @GetMapping("/teams")
    public String teams(
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page ,
            @RequestParam(value = "size", defaultValue = "20") int size
    ){
        List<TeamListResponse> teams = teamService.findAllJoinMember(PageRequest.of(page, size))
                .stream().map(TeamListResponse::new).collect(Collectors.toList());
        model.addAttribute("teams", teams);
        return "team/teamHome";
    }

    @GetMapping("/teams/{teamId}")
    public String teamView(@PathVariable("teamId") Long teamId, Model model){
        List<JoinMemberResponse> resultList = joinTeamService.findAllByTeam(teamId)
                .stream().map(j -> new JoinMemberResponse(
                        j.getMember().getId(), j.getMember().getName(), j.getMember().getNickname(),
                        j.getJoinState(), j.getJoinDate())).collect(Collectors.toList());

        Team team = teamService.findById(teamId);
        TeamResponse teamResponse = new TeamResponse(
                team.getId(), team.getName(), team.getIntroduction(), team.getMember().getId(), team.getMember().getNickname() ,
                resultList, team.getCreatedDate());
        model.addAttribute("team", teamResponse);
        model.addAttribute("teamMembers", resultList.stream().filter(j-> Objects.equals(j.getJoinState().toString(), "OK")).collect(Collectors.toList()));
        return "team/team";
    }

    @GetMapping("/team")
    public String teamForm(Model model,@Login Long memberId){

        log.info("login id = {}", memberId);
        if(memberId == null){
            return "redirect:/login?redirectURI=/team";
        }

        model.addAttribute("team", new TeamForm());

        return "team/addTeamForm";

    }

    @PostMapping("/team")
    public String createTeamForm(
            @Login Long memberId,
            @Validated @ModelAttribute("team") TeamForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            return "team/addTeamForm";
        }

        log.info("loginMemberId = {}",memberId);

        if(memberId == null){
            return "redirect:/login?redirectURI=/team";
        }

        Long teamId = teamService.createTeam(form.getName(), form.getIntroduction(), memberId);
        redirectAttributes.addAttribute("teamId", teamId);
        return "redirect:/teams";
    }

    @GetMapping("/teams/{teamId}/edit")
    public String editTeamForm(@Login Long memberId, @PathVariable("teamId") Long teamId, Model model){
        Team team = teamService.findById(teamId);
        if(!team.getMember().getId().equals(memberId)){
            return "redirect:/teams/"+ teamId;
        }
        model.addAttribute("team",team);
        return "team/editTeamForm";
    }

    @PostMapping("/teams/{teamId}/edit")
    public String editTeam(
            @Login Long memberId, @PathVariable("teamId") Long teamId,
            @Validated @ModelAttribute("team") TeamForm form, BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return "redirect:/teams/"+teamId;
        }

        if(memberId == null){
            return "redirect:/teams/"+teamId;
        }

        teamService.editTeam(teamId, form.getName(), form.getIntroduction(), memberId);

        return "redirect:/teams/"+teamId;
    }

    @PostMapping("/team/{teamId}/state")
    @ResponseBody
    public void changeState(@PathVariable("teamId") Long teamId, @Login Long memberId,
                            @RequestParam("state") String state, @RequestParam("memberId") Long joinMemberId){

        JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(joinMemberId, teamId);

        Team team = joinTeam.getTeam();

        if(!team.getMember().getId().equals(memberId)){
            throw new IllegalStateException("권한이 없습니다");
        };

        String s = state.toUpperCase();
        log.info("state = [{}][{}]", state, s );
        if(s.equals("OK") || s.equals("WAITING") || s.equals("BAN")) {
            teamService.changeState(teamId,joinMemberId,JoinState.valueOf(s));
        }else if(s.equals("REJECT")){
            teamService.doReject(teamId,joinMemberId);
        }else{
            throw new IllegalStateException("상태변경에 실패했습니다");
        }
    }


    @Data
    class TeamForm{
        private String name;
        private String introduction;
    }

    @Data
    class TeamListResponse {

        private Long id;
        private String name;
        private String introduction;
        private Long leaderId;
        private String leaderNickname;
        private LocalDateTime createDate;

        public TeamListResponse(Team team) {
            id = team.getId();
            name = team.getName();
            introduction = team.getIntroduction();
            leaderId = team.getMember().getId();
            leaderNickname = team.getMember().getNickname();
            this.createDate = team.getCreatedDate();
        }
    }

    @Data
    @AllArgsConstructor
    class TeamResponse{
        private Long teamId;
        private String name;
        private String introduction;
        private Long leaderId;
        private String leaderNickname;
        private List<JoinMemberResponse> joinMembers;
        private LocalDateTime createDate;
    }

    @Data
    @AllArgsConstructor
    class JoinMemberResponse{
        private Long memberId;
        private String memberName;
        private String memberNickname;
        private JoinState joinState;
        private LocalDateTime joinDate;

    }

}
