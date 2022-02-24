package team.project.Repository.Team;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Comments;
import team.project.Entity.TeamEntity.FreeComments;

import java.util.List;
import java.util.Optional;

public interface FreeCommentsRepository extends JpaRepository<FreeComments,Long> {

    @Query("SELECT c FROM FreeComments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE ct.id = :contentId")
    List<FreeComments> findAllByContent(@Param("contentId") Long contentId);

    @Query("SELECT c FROM FreeComments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE ct.id = :contentId")
    List<FreeComments> findAllByContent(@Param("contentId") Long contentId, Pageable pageable);

    @Query("SELECT c FROM FreeComments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE c.id = :commentsId")
    Optional<FreeComments> findFetchById(@Param("commentsId") Long commentsId);

}
