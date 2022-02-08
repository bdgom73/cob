package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.project.Entity.ProjectCategory;

import java.util.List;

public interface ProjectCategoryRepository extends JpaRepository<ProjectCategory, Long> {

    @Query("SELECT pc FROM ProjectCategory pc WHERE pc.team.id = :teamId")
    List<ProjectCategory> findByTeam(@Param("teamId") Long teamId);

}
