package team.project.Repository.Team;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Team;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team,Long> {

    @Query("SELECT t FROM Team t JOIN FETCH t.member m")
    List<Team> findByAllJoinMember();

    @Query("SELECT t FROM Team t JOIN FETCH t.member m ")
    List<Team> findByAllJoinMember(Pageable pageable);

    @Query("SELECT t FROM Team t JOIN FETCH t.member m WHERE t.id = :teamId")
    Optional<Team> findMemberById(@Param("teamId") Long teamId );

    Optional<Team> findByName(String name);

    @Query("SELECT count(t) FROM Team t WHERE t.member.id =:memberId")
    long countByMemberId(@Param("memberId") Long memberId);
}
