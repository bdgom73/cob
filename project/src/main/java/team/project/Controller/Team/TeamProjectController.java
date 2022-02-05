package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import team.project.Controller.Form.ProjectTeamForm.ProjectResponse;
import team.project.Dto.CreateProjectDto;
import team.project.Entity.Member;
import team.project.Entity.Project;
import team.project.Repository.JoinTeamRepository;
import team.project.Repository.TeamRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.JoinTeamService;
import team.project.Service.ProjectService;
import team.project.Service.TeamService;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamProjectController {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final JoinTeamRepository joinTeamRepository;
    private final TeamRepository teamRepository;
    private final ProjectService projectService;

    @GetMapping("/teams/{teamId}/projects")
    public String teamProject(@PathVariable("teamId") Long teamId, Model model){
        List<ProjectResponse> resultList = projectService.findAllByTeam(teamId).stream().map(p -> {
            Member member = p.getMember();
            return new ProjectResponse(p.getId(), p.getTitle(), p.getIntroduction(), p.getStartDate(), p.getEndDate(), p.getProgress(),
                    member.getId(), member.getName(), member.getNickname());
        }).collect(Collectors.toList());

        model.addAttribute("projects", resultList);
        return "project/projectHome";
    }

    @GetMapping("/teams/{teamId}/projects/create")
    public String teamProjectForm(@PathVariable("teamId") Long teamId, Model model){
        model.addAttribute("project",new ProjectForm());
        return "project/addProjectForm";
    }

    @PostMapping("/teams/{teamId}/projects/create")
    public String createTeamProjectForm(@PathVariable("teamId") Long teamId, @Login Long memberId, @ModelAttribute("project") ProjectForm form, BindingResult bindingResult, RedirectAttributes redirectAttributes){
        if(bindingResult.hasErrors()){
            return "redirect:/team/"+teamId+"/projects/create";
        }
        Long projectId = projectService.createProject(memberId, new CreateProjectDto(form.getTitle(), form.getIntroduction(), form.getStartDate(), form.getEndDate(), teamId));
        redirectAttributes.addAttribute("projectId", projectId);
        return "redirect:/teams/"+teamId+"/projects/"+projectId;
    }

    @Data
    class ProjectForm{
        @NotBlank
        private String title;
        private String introduction;
        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
        private LocalDateTime startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm")
        private LocalDateTime endDate;
    }

}
