package team.project.Repository.Team.ContentStat;

import team.project.Repository.Team.JoinTeamStat.StatCount;

import java.time.LocalDate;
import java.util.List;

public interface StatFreeContentRepository {

    List<StatCount> findByStatDate(LocalDate date, Long teamId);

}
