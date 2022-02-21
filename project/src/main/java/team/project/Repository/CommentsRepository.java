package team.project.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Comments;

import java.util.List;
import java.util.Optional;

public interface CommentsRepository extends JpaRepository<Comments, Long> {

    @Query("SELECT c FROM Comments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE ct.id = :contentId")
    List<Comments> findAllByContent(@Param("contentId") Long contentId);

    @Query("SELECT c FROM Comments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE ct.id = :contentId")
    List<Comments> findAllByContent(@Param("contentId") Long contentId, Pageable pageable);

    @Query("SELECT c FROM Comments c JOIN FETCH c.member m JOIN FETCH c.content ct WHERE c.id = :commentsId")
    Optional<Comments> findFetchById(@Param("commentsId") Long commentsId);
}
