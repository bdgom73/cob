package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.JoinState;
import team.project.Entity.JoinTeam;
import team.project.Entity.Member;
import team.project.Entity.Team;
import team.project.Repository.JoinTeamRepository;
import team.project.Repository.TeamRepository;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberService memberService;
    private final JoinTeamRepository joinTeamRepository;
    private final JoinTeamService joinTeamService;

    @Transactional
    public Long createTeam(String teamName, String introduction, Long memberId){
        Member member = memberService.findById(memberId);
        Team team = Team.createTeam(teamName, introduction, member);
        teamRepository.save(team);
        return team.getId();
    }

    @Transactional
    public Long applyTeam(Long teamId, Long memberId){
        Team team = findById(teamId);
        Member member = memberService.findById(memberId);
        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        joinTeamRepository.save(joinTeam);
        return team.getId();
    }

    @Transactional
    public void doAccept(Long teamId, Long memberId){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        joinTeam.changeState(JoinState.OK);
    }

    @Transactional
    public void doBan(Long teamId, Long memberId){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        joinTeam.changeState(JoinState.BAN);
    }

    @Transactional
    public void doUnBan(Long teamId, Long memberId){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        joinTeamRepository.delete(joinTeam);
    }

    @Transactional
    public void doReject(Long teamId, Long memberId){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        joinTeamRepository.delete(joinTeam);
    }

    public Team findById(Long teamId){
        return teamRepository.findById(teamId).orElseThrow(()->{
           throw new IllegalStateException("존재하지 않는 팀 입니다");
        });
    }

    public List<Team> findAll(){
        PageRequest pageRequest = PageRequest.of(0, 100);
        return teamRepository.findAll(pageRequest).getContent();
    }

    public List<Team> findAllJoinMember(Pageable pageable){
        return teamRepository.findByAllJoinMember(pageable);
    }
}
