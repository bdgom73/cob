package team.project.Repository.Team.ContentStat;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.project.Repository.Team.JoinTeamStat.StatCount;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class StatFreeContentRepositoryImpl implements StatFreeContentRepository {

    private final EntityManager em;

    @Override
    public List<StatCount> findByStatDate(LocalDate date, Long teamId) {
        String query = "SELECT new team.project.Repository.Team.JoinTeamStat.StatCount(";
        query += " SUBSTRING(fc.createdDate,0,7) ,";
        query += " count(fc)";
        query += " ) FROM FreeContent fc";
        query += " WHERE fc.createdDate between '"+ date.minusMonths(6).format(DateTimeFormatter.ofPattern("yyyy-MM-01")) + "' AND '" +
                date.withDayOfMonth(date.lengthOfMonth()) +
                "' AND fc.team.id = :teamId";
        query += " GROUP BY SUBSTRING(fc.createdDate,0,7)";
        return em.createQuery(query, StatCount.class)
                .setParameter("teamId", teamId)
                .getResultList();
    }
}
