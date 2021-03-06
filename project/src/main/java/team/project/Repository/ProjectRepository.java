package team.project.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p JOIN FETCH p.member m WHERE p.team.id = :teamId")
    List<Project> findMemberAllByTeam(@Param("teamId") Long teamId);

    @Query("SELECT p FROM Project p JOIN FETCH p.member m WHERE p.team.id = :teamId")
    List<Project> findMemberAllByTeam(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN FETCH p.member m JOIN FETCH p.team t WHERE p.team.id = :teamId")
    List<Project> findAllByTeam(@Param("teamId") Long teamId);

    @Query("SELECT p FROM Project p JOIN FETCH p.member m JOIN FETCH p.team t WHERE p.team.id = :teamId")
    List<Project> findAllByTeam(@Param("teamId") Long teamId, Pageable pageable);

    @Query("SELECT p FROM Project p JOIN FETCH p.member m WHERE p.id = :projectId")
    Optional<Project> findMemberById(@Param("projectId") Long projectId);

    @Query("SELECT p FROM Project p JOIN FETCH p.team t WHERE p.id = :projectId")
    Optional<Project> findTeamById(@Param("projectId") Long projectId);

    @Query("SELECT p FROM Project p JOIN FETCH p.team t JOIN FETCH p.member m WHERE p.id = :projectId")
    Optional<Project> findTeamAndMemberById(@Param("projectId") Long projectId);
}
