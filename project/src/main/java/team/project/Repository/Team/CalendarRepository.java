package team.project.Repository.Team;

import org.apache.tomcat.jni.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Calendar;
import team.project.Entity.TeamEntity.Team;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {

    @Query("SELECT c FROM Calendar c WHERE c.startDate BETWEEN :start AND :end OR c.endDate BETWEEN :start AND :end  AND c.team = :team ")
    List<Calendar> findAllByTeamRangeDate(@Param("team") Team team,@Param("start") LocalDateTime start,@Param("end") LocalDateTime end);

    List<Calendar> findByGroupId(Long groupId);

    @Query("SELECT c FROM Calendar c WHERE c.team.id = :teamId")
    List<Calendar> findByTeamId(@Param("teamId") Long teamId);
}
