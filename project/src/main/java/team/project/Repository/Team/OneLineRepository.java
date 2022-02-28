package team.project.Repository.Team;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.OneLine;
import team.project.Entity.TeamEntity.Team;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OneLineRepository extends JpaRepository<OneLine,Long> {

    @Query("SELECT o FROM OneLine o JOIN FETCH o.member m WHERE o.team.id = :teamId AND o.date = :date")
    List<OneLine> findByTeamAndDate(@Param("teamId") Long teamId ,@Param("date") LocalDate date);

    @Query("SELECT o FROM OneLine o WHERE o.team.id = :teamId")
    List<OneLine> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT o FROM OneLine o JOIN FETCH o.member m WHERE o.id = :id")
    Optional<OneLine> findMemberById(@Param("id") Long id);
}
