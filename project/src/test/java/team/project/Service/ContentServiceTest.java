package team.project.Service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import team.project.Dto.Content.CreateContentDto;
import team.project.Dto.CreateProjectDto;
import team.project.Entity.*;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@Rollback(false)
class ContentServiceTest {

    @Autowired
    EntityManager em;

    @Autowired
    ProjectService projectService;

    @Autowired TeamService teamService;
    @Autowired ContentService contentService;

    @Test
    void writeContent() {
        Member testpro1 = getMember("testpro1", "su");
        Team team = getTeam(testpro1);

        CreateProjectDto team_project =
                new CreateProjectDto("team project", "pro is ...", LocalDateTime.now(), LocalDateTime.now(), team.getId());

        Long projectId = projectService.createProject(testpro1.getId(), team_project);

        Member member = getMember("joinMember1", "userBBC");
        teamService.applyTeam(team.getId(), member.getId());
        teamService.doAccept(team.getId(), member.getId());

        Member member2 = getMember("joinMember2", "userBBC");
        teamService.applyTeam(team.getId(), member2.getId());
        teamService.doAccept(team.getId(), member2.getId());

        System.out.println("============================START================================== ");
        projectService.addProjectMember(team.getId(),projectId, member.getId(), member2.getId());

        ProjectCategory testCategory = contentService.createCategory(team.getId(), "테스트");

        CreateContentDto createContentDto = new CreateContentDto("test title", "test is very good", projectId, testCategory.getId());
        Long aLong = contentService.writeContent(member2.getId(), createContentDto);
    }

    private Team getTeam(Member member) {
        Team team = new Team("teamA", "teamA is ...");
        team.setMember(member);
        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        joinTeam.changeState(JoinState.OK);
        em.persist(team);
        return team;
    }

    private Member getMember(String userA, String su) {
        Member member = new Member(userA, "1111", userA, su, RoleType.USER);

        em.persist(member);

        return member;
    }
}