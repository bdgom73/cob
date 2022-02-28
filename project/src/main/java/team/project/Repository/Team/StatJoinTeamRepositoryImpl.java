package team.project.Repository.Team;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Repository
@Transactional
public class StatJoinTeamRepositoryImpl implements StatJoinTeamRepository{

    private final EntityManager em;

    @Override
    public List<StatCount> findByStatDate(LocalDate date, Long teamId) {
        String query = "SELECT new team.project.Repository.Team.StatCount(";
        query += " SUBSTRING(j.createdDate,0,7) ,";
        query += " count(j)";
        query += " ) FROM JoinTeam j";
        query += " WHERE j.createdDate between '"+ date.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-01")) + "' AND '" +
                date.withDayOfMonth(date.lengthOfMonth()) +
                "' AND j.team.id = :teamId";
        query += " GROUP BY SUBSTRING(j.createdDate,0,7)";
        log.info("query : {}",query);
        return em.createQuery(query, StatCount.class)
                .setParameter("teamId", teamId)
                .getResultList();

    }
}
