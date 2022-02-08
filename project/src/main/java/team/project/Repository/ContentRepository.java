package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.Entity.TeamEntity.Content;

public interface ContentRepository extends JpaRepository<Content, Long> {
}
