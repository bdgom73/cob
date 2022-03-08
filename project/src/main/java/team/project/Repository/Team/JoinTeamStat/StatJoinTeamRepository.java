package team.project.Repository.Team.JoinTeamStat;

import java.time.LocalDate;
import java.util.List;

public interface StatJoinTeamRepository {

    List<StatCount> findByStatDate(LocalDate date, Long teamId);

}
