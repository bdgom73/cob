package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Controller.Form.TeamForm.JoinMemberResponse;
import team.project.Entity.JoinState;
import team.project.Entity.JoinTeam;
import team.project.Entity.Member;
import team.project.Repository.JoinTeamRepository;

import java.util.List;
import java.util.stream.Collectors;

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
    public JoinTeam findByMemberInTeam(Long memberId, Long teamId){
        return joinTeamRepository.findByMemberAndTeam(memberId, teamId).orElseThrow(() -> {
            throw new IllegalStateException("내역이 없습니다");
        });
    }
    public JoinTeam findMemberByMemberInTeam(Long memberId, Long teamId){
        return joinTeamRepository.findMemberByMemberAndTeam(memberId, teamId).orElseThrow(() -> {
            throw new IllegalStateException("내역이 없습니다");
        });
    }
    public JoinTeam findMemberAndTeamByMemberInTeam(Long memberId, Long teamId){
        return joinTeamRepository.findMemberAndTeamByMemberAndTeam(memberId, teamId).orElseThrow(() -> {
            throw new IllegalStateException("내역이 없습니다");
        });
    }

    public List<JoinTeam> findMemberByTeamAndState(Long teamId, JoinState state){
        return joinTeamRepository.findMemberByTeamAndState(teamId, state);
    }
    public List<JoinTeam> findAllByTeamAndState(Long teamId, JoinState state){
        return joinTeamRepository.findAllByTeamAndState(teamId, state);
    }
    public List<JoinTeam> findAllByTeamAndNotState(Long teamId, JoinState state){
        return joinTeamRepository.findAllByTeamAndNotState(teamId, state);
    }
    public List<JoinTeam> findAllByTeam(Long teamId){
        return joinTeamRepository.findAllByTeam(teamId);
    }

    public List<JoinMemberResponse> JoinTeamToResultList(Long teamId, JoinState state) {
        return findAllByTeamAndState(teamId, state).stream()
                .map(j -> {
                    Member member = j.getMember();
                    return new JoinMemberResponse(
                            j.getId(), member.getId(), member.getName(), member.getNickname(),
                            j.getJoinState(), j.getJoinDate()
                    );
                }).collect(Collectors.toList());
    }
}
