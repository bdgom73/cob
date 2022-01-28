package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.Entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
