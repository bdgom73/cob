package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.JoinState;
import team.project.Entity.JoinTeam;

import java.util.List;
import java.util.Optional;

public interface JoinTeamRepository extends JpaRepository<JoinTeam, Long> {

    @Query("SELECT j FROM JoinTeam j WHERE j.member.id = :memberId AND j.team.id = :teamId")
    Optional<JoinTeam> findByMemberAndTeam(@Param("memberId") Long memberId,@Param("teamId") Long teamId);

    @Query("SELECT j FROM JoinTeam j JOIN FETCH j.member m WHERE m.id = :memberId AND j.team.id = :teamId")
    Optional<JoinTeam> findMemberByMemberAndTeam(@Param("memberId") Long memberId,@Param("teamId") Long teamId);

    @Query("SELECT j FROM JoinTeam j JOIN FETCH j.member m JOIN FETCH j.team t WHERE m.id = :memberId AND t.id = :teamId")
    Optional<JoinTeam> findMemberAndTeamByMemberAndTeam(@Param("memberId") Long memberId,@Param("teamId") Long teamId);

    @Query("SELECT j FROM JoinTeam j JOIN FETCH j.member m WHERE j.team.id = :teamId AND j.joinState = :state")
    List<JoinTeam> findMemberByTeamAndState(@Param("teamId") Long teamId ,@Param("state") JoinState state);

    @Query("SELECT j FROM JoinTeam j JOIN FETCH j.member m JOIN FETCH j.team t WHERE j.team.id = :teamId AND j.joinState = :state")
    List<JoinTeam> findAllByTeamAndState(@Param("teamId") Long teamId ,@Param("state") JoinState state);

    @Query("SELECT j FROM JoinTeam j JOIN FETCH j.member m JOIN FETCH j.team t WHERE j.team.id = :teamId")
    List<JoinTeam> findAllByTeam(@Param("teamId") Long teamId);
}
