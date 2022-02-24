package team.project.Repository.Team;

import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Repository.Team.StatCount;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StatJoinTeamRepository {

    List<StatCount> findByStatDate(LocalDate date, Long teamId);

}
