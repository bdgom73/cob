package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.OneLine;
import team.project.Entity.TeamEntity.Team;

import java.time.LocalDate;
import java.util.List;

public interface OneLineRepository extends JpaRepository<OneLine,Long> {

    @Query("SELECT o FROM OneLine o JOIN FETCH o.member m WHERE o.team.id = :teamId AND o.date = :date")
    List<OneLine> findByTeamAndDate(@Param("teamId") Long teamId ,@Param("date") LocalDate date);
}