package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.OneLine;
import team.project.Entity.TeamEntity.Team;
import team.project.Repository.MemberRepository;
import team.project.Repository.Team.OneLineRepository;
import team.project.Repository.Team.TeamRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OneLineService {

    private final OneLineRepository oneLineRepository;
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void writeOneLine(String text, Team team, Member member){
        OneLine oneLine = new OneLine(text, team, member);
        oneLineRepository.save(oneLine);
    }
    @Transactional
    public void writeOneLine(String text, Long teamId, Long memberId){
        Team team = teamRepository.findById(teamId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalStateException();
        });
        OneLine oneLine = new OneLine(text, team, member);
        oneLineRepository.save(oneLine);
    }

    public List<OneLine> findByDate(Long teamId, LocalDate date){
        return oneLineRepository.findByTeamAndDate(teamId, date);
    }
}
