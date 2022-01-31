package team.project.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import team.project.Entity.Team;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team,Long> {

    @Query("SELECT t FROM Team t JOIN FETCH t.member m")
    List<Team> findByAllJoinMember();

    @Query("SELECT t FROM Team t JOIN FETCH t.member m ")
    List<Team> findByAllJoinMember(Pageable pageable);
}
