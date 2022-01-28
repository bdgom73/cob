package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.JoinState;
import team.project.Entity.JoinTeam;
import team.project.Repository.JoinTeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinTeamService {

    private final JoinTeamRepository joinTeamRepository;

    public JoinTeam findById(Long id){
        return joinTeamRepository.findById(id).orElseThrow(() -> {
            throw new IllegalStateException("내역이 없습니다");
        });
    }
    public JoinTeam findByMemberIdAndTeamId(Long memberId, Long teamId){
        return joinTeamRepository.findByMemberAndTeam(memberId, teamId).orElseThrow(() -> {
            throw new IllegalStateException("내역이 없습니다");
        });
    }
    public List<JoinTeam> findMemberByTeamAndState(Long teamId, JoinState state){
        return joinTeamRepository.findMemberByTeamAndState(teamId, state);
    }
    public List<JoinTeam> findAllByTeamAndState(Long teamId, JoinState state){
        return joinTeamRepository.findAllByTeamAndState(teamId, state);
    }
}
