package team.project.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.TeamEntity.Content;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c FROM Content c JOIN FETCH c.project p JOIN FETCH c.member m WHERE p.id = :projectId")
    List<Content> findByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT c FROM Content c JOIN FETCH c.project p JOIN FETCH c.member m WHERE p.id = :projectId")
    List<Content> findByProjectId(@Param("projectId") Long projectId, Pageable pageable);
}
