package team.project.Repository.Team;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.Progress;
import team.project.Entity.TeamEntity.Content;

import java.util.List;
import java.util.Optional;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c FROM Content c WHERE c.project.id = :projectId")
    List<Content> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT c FROM Content c JOIN FETCH c.project p JOIN FETCH c.member m WHERE p.id = :projectId")
    List<Content> findFetchByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT c FROM Content c JOIN FETCH c.project p JOIN FETCH c.member m WHERE p.id = :projectId")
    List<Content> findFetchByProjectId(@Param("projectId") Long projectId, Pageable pageable);

    @Query("SELECT c FROM Content c JOIN FETCH c.project p JOIN FETCH c.member m WHERE p.id = :projectId AND c.category = :order")
    List<Content> findOrderByProjectId(@Param("projectId") Long projectId, @Param("order") Progress order , Pageable pageable);

    @Query("SELECT c FROM Content c JOIN FETCH c.member m WHERE c.id = :contentId")
    Optional<Content> findMemberById(@Param("contentId") Long id);

    @Query("SELECT c FROM Content c JOIN FETCH c.member m JOIN FETCH c.project p WHERE c.id = :contentId")
    Optional<Content> findFetchMemberAndProjectById(@Param("contentId") Long id);

    @Query("SELECT c FROM Content c JOIN FETCH c.member m JOIN FETCH c.project p WHERE c.category = :category AND c.project.id = :projectId")
    List<Content> findAllByCategory(@Param("projectId") Long projectId ,@Param("category") Progress progress);

}
