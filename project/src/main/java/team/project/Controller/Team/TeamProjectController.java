package team.project.Controller.Team;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.project.Controller.Form.ProjectTeamForm.ProjectResponse;
import team.project.Dto.CreateProjectDto;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.Project;
import team.project.Repository.Team.CalendarRepository;
import team.project.Service.ArgumentResolver.Login;
import team.project.Service.ProjectService;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamProjectController {

    private final ProjectService projectService;
    private final CalendarRepository calendarRepository;

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
    public String createTeamProjectForm(@PathVariable("teamId") Long teamId, @Login Long memberId, @ModelAttribute("project") ProjectForm form, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.info("bindingResult {}", bindingResult);
            return "redirect:/teams/"+teamId+"/projects/create";
        }
        Long projectId = projectService.createProject(memberId, new CreateProjectDto(form.getTitle(), form.getIntroduction(), form.getStartDate(), form.getEndDate(), teamId));
        return "redirect:/teams/"+teamId+"/projects";
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}")
    public String teamProject(@PathVariable("teamId") Long teamId, @PathVariable("projectId") Long projectId, Model model){
        Project project = projectService.findTeamAndMemberById(projectId);
        ProjectResponse projectResponse = new ProjectResponse(project.getId(), project.getTitle(), project.getIntroduction(), project.getStartDate(), project.getEndDate(), project.getProgress(), project.getMember().getId(), project.getMember().getName(), project.getMember().getNickname());
        model.addAttribute("project", projectResponse);
        model.addAttribute("nlString", System.getProperty("line.separator"));
        return "project/project";
    }

    @GetMapping("/teams/{teamId}/projects/{projectId}/edit")
    public String editTeamProjectForm(@PathVariable("projectId") Long projectId, Model model){
        Project project = projectService.findTeamAndMemberById(projectId);
        ProjectForm projectForm = new ProjectForm();
        projectForm.setTitle(project.getTitle());
        projectForm.setIntroduction(project.getIntroduction());
        projectForm.setStartDate(project.getStartDate());
        projectForm.setEndDate(project.getEndDate());
        model.addAttribute("project", projectForm);
        return "project/editProjectForm";
    }

    @PostMapping("/teams/{teamId}/projects/{projectId}/edit")
    public String editTeamProjectForm(
            @Validated @ModelAttribute("project") ProjectForm form, BindingResult bindingResult,
            @PathVariable("projectId") Long projectId  , @PathVariable("teamId") Long teamId,
            @RequestAttribute("checkJoinTeam") JoinTeam joinTeam,
            @SessionAttribute(name = "UID",required = false) Long memberId
    ){
        if(bindingResult.hasErrors()){
            log.info("bindingResult {}", bindingResult);
            return "redirect:/teams/"+teamId+"/projects/"+projectId+"/edit";
        }

        Project project = projectService.findMemberById(projectId);

        if(Objects.equals(joinTeam.getTeam().getMember().getId(), memberId) || Objects.equals(project.getMember().getId(), memberId)){
            projectService.changeProject(project, form.getTitle(), form.getIntroduction(), form.getStartDate(), form.getEndDate());
            return "redirect:/teams/"+teamId+"/projects/"+projectId;
        }

        return "redirect:/teams/"+teamId+"/projects/"+projectId+"/edit";
    }

    @PostMapping("/teams/{teamId}/projects/{projectId}/edit/progress")
    public String editTeamProjectProgress(
            @RequestParam("projectProgress") String progress,
            @PathVariable("projectId") Long projectId  , @PathVariable("teamId") Long teamId,
            @RequestAttribute("checkJoinTeam") JoinTeam joinTeam,
            @SessionAttribute(name = "UID",required = false) Long memberId
    ){
        Project project = projectService.findMemberById(projectId);
        if(Objects.equals(joinTeam.getTeam().getMember().getId(), memberId) || Objects.equals(project.getMember().getId(), memberId)){
            projectService.changeProgress(project, Progress.valueOf(progress));
        }
        return "redirect:/teams/"+teamId+"/projects/"+projectId;
    }

    @PostMapping("/teams/{teamId}/projects/{projectId}/delete")
    public String deleteProject(@PathVariable("projectId") Long projectId,@PathVariable("teamId") Long teamId){
        try{
            projectService.deleteProject(projectId);
            return "redirect:/teams"+teamId+"/projects";
        }catch (Exception e){
            return "redirect:/teams"+teamId+"/projects/"+projectId;
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

}
