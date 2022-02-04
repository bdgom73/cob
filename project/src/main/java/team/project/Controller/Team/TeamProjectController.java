package team.project.Controller.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import team.project.Entity.Project;
import team.project.Repository.JoinTeamRepository;
import team.project.Repository.TeamRepository;
import team.project.Service.JoinTeamService;
import team.project.Service.ProjectService;
import team.project.Service.TeamService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class TeamProjectController {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;
    private final JoinTeamRepository joinTeamRepository;
    private final TeamRepository teamRepository;
    private final ProjectService projectService;

//    @GetMapping("/teams/{teamId}/projects")
//    public void teamProject(@PathVariable("teamId") Long teamId, Model model){
//        List<Project> projects = projectService.findAllByTeam(teamId);
//
//    }

}
