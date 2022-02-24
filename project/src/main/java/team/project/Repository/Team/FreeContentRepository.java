package team.project.Repository.Team;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.Content;
import team.project.Entity.TeamEntity.FreeContent;

import java.util.List;
import java.util.Optional;

public interface FreeContentRepository extends JpaRepository<FreeContent, Long> {

    @Query("SELECT c FROM FreeContent c JOIN FETCH c.member m WHERE c.id = :contentId")
    Optional<FreeContent> findMemberById(@Param("contentId") Long id);

    @Query("SELECT c FROM FreeContent c JOIN FETCH c.member m  WHERE c.id = :contentId")
    Optional<FreeContent> findFetchMemberAndProjectById(@Param("contentId") Long id);

    @Query("SELECT c FROM FreeContent c JOIN FETCH c.member m JOIN FETCH c.team t WHERE t.id = :teamId")
    List<FreeContent> findMemberAndTeamByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT c FROM FreeContent c JOIN FETCH c.member m WHERE c.team.id = :teamId")
    List<FreeContent> findMemberAndTeamByTeamId(@Param("teamId") Long teamId, Pageable pageable);
}
