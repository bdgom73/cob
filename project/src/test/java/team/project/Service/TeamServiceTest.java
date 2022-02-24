package team.project.Service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.*;
import team.project.Entity.TeamEntity.Team;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Repository.Team.JoinTeamRepository;
import team.project.Repository.MemberRepository;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@Rollback(value = false)
class TeamServiceTest {

    @Autowired
    TeamService teamService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;
    @Autowired
    JoinTeamRepository joinTeamRepository;
    @Test
    void createTeam() {
        //given
        Member userA = getMember("userA");
        memberRepository.save(userA);
        //when
        Long teamA = teamService.createTeam("teamA", "teamA is ...", userA.getId());

        em.flush();
        em.clear();

        Team team = teamService.findById(teamA);

        //then
        Assertions.assertThat(teamA).isEqualTo(team.getId());
    }

    private Long initTeam(String name , Long memberId) {
        return teamService.createTeam(name , name+" is ...", memberId);
    }

    private Member getMember(String name) {
        return new Member(name, "1111", name, name, RoleType.USER);
    }

    @Test
    void applyTeam() {
        //given
        Member admin = getMember("admin");
        Member userA = getMember("userA");
        memberRepository.save(admin);
        memberRepository.save(userA);
        Long teamA = initTeam("teamA", admin.getId());
        //when
        Long teamId = teamService.applyTeam(teamA, userA.getId());

        //then
        Assertions.assertThat(teamId).isEqualTo(teamA);
    }

    @Test
    void accept() {
        //given
        Member admin = getMember("admin");
        Member userA = getMember("userA");
        memberRepository.save(admin);
        memberRepository.save(userA);

        Long teamAId = initTeam("teamA", admin.getId());
        teamService.applyTeam( teamAId, userA.getId() );
        //when
        teamService.doAccept(teamAId, userA.getId());

        em.flush();
        em.clear();

        JoinTeam joinTeam = joinTeamRepository.findByMemberAndTeam(userA.getId(), teamAId).get();

        //then
        Assertions.assertThat(joinTeam.getJoinState()).isEqualTo(JoinState.OK);
    }

    @Test
    void expulsion() {
        //given
        Member admin = getMember("admin");
        Member userA = getMember("userA");
        memberRepository.save(admin);
        memberRepository.save(userA);
        Long teamA = initTeam("teamA", admin.getId());
        teamService.applyTeam(teamA, userA.getId());
        //when
        teamService.doBan(teamA, userA.getId());

        em.flush();
        em.clear();

        JoinTeam joinTeam = joinTeamRepository.findByMemberAndTeam(userA.getId(), teamA).get();
        //then
        Assertions.assertThat(joinTeam.getJoinState()).isEqualTo(JoinState.BAN);
    }

    @Test
    void findById() {
        //given
        Member admin = getMember("admin");
        memberRepository.save(admin);

        Long teamA = initTeam("teamA", admin.getId());

        em.flush();
        em.clear();
        //when
        Team t = teamService.findById(teamA);
        //then
        Assertions.assertThat(t.getName()).isEqualTo("teamA");
    }
}