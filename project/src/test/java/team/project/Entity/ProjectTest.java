package team.project.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class ProjectTest {

    @Autowired
    EntityManager em;

    @Test
    void createProjectTest(){
        Member member = new Member("memberA@","1111","memberA","userA", RoleType.USER);
        em.persist(member);
        Team team = Team.createTeam("TeamA", "teamA is good", member);
        em.persist(team);

        LocalDateTime startDate = LocalDateTime.of(2022, Month.FEBRUARY, 1, 0,0);
        LocalDateTime endDate = LocalDateTime.of(2022, Month.FEBRUARY, 20, 0,0);
        Project project = Project.create("Spring JPA TEST", "이건 뭐 이런겁니다",startDate, endDate, team, member);
        em.persist(project);
    }
}