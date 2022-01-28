package team.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import team.project.Entity.Team;

public interface TeamRepository extends JpaRepository<Team,Long> {
}
