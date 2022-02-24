package team.project.Repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.project.Repository.Team.JoinTeamRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@SpringBootTest
@Transactional
class StatJoinTeamRepositoryImplTest {

    @Autowired
    JoinTeamRepository joinTeamRepository;

    @Test
    void countByDate() {
        // function('date_format', j.joinDate, '%Y, %m')
//        List<StatCount> statCounts = joinTeamRepository.findByStatDate(LocalDate.of(2022, 4, 1), 2L);
//        Map<String, Long> map = statCounts.stream().collect(Collectors.toMap(StatCount::getDate, StatCount::getCount));

//        System.out.println("map = " + map.get("2022-01"));
    }
}