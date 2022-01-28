package team.project.Entity;

import org.assertj.core.api.Assertions;
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
class TeamTest {

    @Autowired
    EntityManager em;

    @Test
    void saveTeamTest(){
        Member member = new Member("memberA@","1111","memberA","userA", RoleType.USER);
        em.persist(member);
        Team team = Team.createTeam("teamA","teamA is ...", member);
        em.persist(team);

        Assertions.assertThat(member.getId()).isEqualTo(team.getMember().getId());
    }
}