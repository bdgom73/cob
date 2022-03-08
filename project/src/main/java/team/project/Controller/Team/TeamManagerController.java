package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team.project.CommonConst;
import team.project.Controller.Form.ProjectTeamForm.ProjectResponse;
import team.project.Controller.Form.TeamForm.JoinMemberResponse;
import team.project.Controller.Form.TeamForm.TeamForm;
import team.project.Controller.Form.TeamForm.TeamsResponse;
import team.project.Entity.Member;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.Project;
import team.project.Entity.TeamEntity.Team;
import team.project.Repository.Team.ContentRepository;
import team.project.Repository.Team.FreeContentRepository;
import team.project.Repository.Team.JoinTeamStat.StatCount;
import team.project.Service.*;
import team.project.Service.ArgumentResolver.Login;

import javax.validation.constraints.NotBlank;
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
    private final ProjectService projectService;
    private final ContentRepository contentRepository;
    private final FreeContentService freeContentService;
    private final FreeContentRepository freeContentRepository;

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
                        c.getId(), c.getTitle(), c.getText(), 0L,
                        m.getId(), m.getNickname(), m.getName(),
                        c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
            }).collect(Collectors.toList());
        }else if(bbs.equals("project")){
            if(!projectId.equals(0L)){
                resultList = contentService.findAllByProjectAndCategory(projectId, Progress.valueOf(category)).stream().map(c -> {
                    Member m = c.getMember();
                    return new BoardResponse(
                            c.getId(), c.getTitle(), c.getText(), c.getProject().getId(),
                            m.getId(), m.getNickname(), m.getName(),
                            c.getCreatedDate(), c.getModifiedDate(), c.getComments().size());
                }).collect(Collectors.toList());
            }else{
                return "redirect:/@teams/"+teamId + "/bbs?bbs=free";
            }
        }
        Map<String, Long> contentStatDate = contentRepository.findByStatDate(LocalDate.now(), teamId).stream().collect(Collectors.toMap(StatCount::getDate, StatCount::getCount));
        Map<String, Long> freeContentStatDate = freeContentRepository.findByStatDate(LocalDate.now(), teamId).stream().collect(Collectors.toMap(StatCount::getDate, StatCount::getCount));

        model.addAttribute("contentStatDate", contentStatDate);
        model.addAttribute("freeContentStatDate", freeContentStatDate);
        model.addAttribute("teamId",teamId);
        model.addAttribute("boards", resultList);
        model.addAttribute("boardType", bbs);
        return "manager/managerBoard";
    }



    @PostMapping("/{teamId}/bbs/delete")
    public String deleteBBS(
            @PathVariable("teamId") Long teamId,
            @RequestParam("contentId") List<Long> boardIds,
            @RequestParam("bbsType") String boardType,
            RedirectAttributes attributes
//            @RequestParam("projectId") Long projectId

    ){
        if(boardType.equals("free")){
            freeContentRepository.deleteAllById(boardIds);
        }else if(boardType.equals("project")){
            contentRepository.deleteAllById(boardIds);
        }else{
            attributes.addFlashAttribute("resultMsg","삭제가 불가능합니다");
            return "redirect:/@teams/"+teamId + "/bbs?bbs=free";
        }
        return "redirect:/@teams/"+teamId + "/bbs?bbs=free";
    }

    @GetMapping("/{teamId}/projects")
    public String managementProjects(
            Model model,
            @PathVariable("teamId") Long teamId,
            RedirectAttributes redirectAttributes,
            @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam
    ){
        List<ProjectResponse> resultList = projectService.findAllByTeam(teamId).stream().map(p -> {
            Member member = p.getMember();
            return new ProjectResponse(p.getId(), p.getTitle(), p.getIntroduction(), p.getStartDate(), p.getEndDate(), p.getProgress(),
                    member.getId(), member.getName(), member.getNickname());
        }).collect(Collectors.toList());
        model.addAttribute("projects", resultList);
        model.addAttribute("teamId",teamId);
        return "manager/managerProjects";
    }

    @PostMapping("/{teamId}/projects/{projectId}/edit")
    public String editTeamProjectForm(
            @Validated @ModelAttribute("project") ProjectForm form, BindingResult bindingResult,
            @PathVariable("projectId") Long projectId  , @PathVariable("teamId") Long teamId,
            @RequestAttribute("checkJoinTeam") JoinTeam joinTeam,
            @SessionAttribute(name = "UID",required = false) Long memberId
    ){
        if(bindingResult.hasErrors()){
            log.info("bindingResult {}", bindingResult);
            return "redirect:/@teams/"+teamId+"/projects";
        }

        Project project = projectService.findMemberById(projectId);

        if(Objects.equals(joinTeam.getTeam().getMember().getId(), memberId) || Objects.equals(project.getMember().getId(), memberId)){
            projectService.changeProject(project, form.getTitle(), form.getIntroduction(), form.getStartDate(), form.getEndDate());
            return "redirect:/@teams/"+teamId+"/projects";
        }

        return "redirect:/@teams/"+teamId+"/projects";
    }

    @PostMapping("/{teamId}/projects/{projectId}/delete")
    public String deleteProject(@PathVariable("projectId") Long projectId,@PathVariable("teamId") Long teamId){
        try{
            projectService.deleteProject(projectId);
            return "redirect:/@teams/"+teamId+"/projects";
        }catch (Exception e){
            return "redirect:/@teams/"+teamId+"/projects/"+projectId;
        }
    }

    @Data
    class ProjectForm{
        @NotBlank
        private String title;
        private String introduction;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
        private LocalDateTime startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:ss")
        private LocalDateTime endDate;

    }

    @Data
    @AllArgsConstructor
    class BoardResponse{
        private Long contentId;
        private String title;
        private String text;
        private Long projectId;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
        private int count;
    }
}
