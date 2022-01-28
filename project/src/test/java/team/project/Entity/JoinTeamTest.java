package team.project.Entity;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class JoinTeamTest {

    @Autowired
    EntityManager em;

    @Test
    void joinTest(){
        Member member = new Member("memberA@","1111","memberA","userA", RoleType.USER);
        em.persist(member);
        Team team = new Team("TeamA", "TeamA is good");
        team.setMember(member);
        em.persist(team);

        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        em.persist(joinTeam);
    }
}