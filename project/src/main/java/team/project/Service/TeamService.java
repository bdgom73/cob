package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.*;
import team.project.Repository.JoinTeamRepository;
import team.project.Repository.MemberRepository;
import team.project.Repository.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final JoinTeamRepository joinTeamRepository;
    private final JoinTeamService joinTeamService;

    @Transactional
    public Long createTeam(String teamName, String introduction, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->{
            throw new UsernameNotFoundException("존재하지 않는 유저입니다");
        });
        Team team = Team.createTeam(teamName, introduction, member);
        teamRepository.save(team);
        return team.getId();
    }

    @Transactional
    public Long applyTeam(Long teamId, Long memberId){
        Team team = findById(teamId);
        Member member = memberRepository.findById(memberId).orElseThrow(()->{
            throw new UsernameNotFoundException("존재하지 않는 유저입니다");
        });
        JoinTeam joinTeam = JoinTeam.applyTeam(team, member);
        joinTeamRepository.save(joinTeam);
        return team.getId();
    }

    @Transactional
    public Long editTeam(Long teamId, String teamName, String introduction, Long memberId){
        Team team = findById(teamId);
        if(!Objects.equals(team.getMember().getId(), memberId)){
            return null;
        }
        team.changeTeam(teamName, introduction);
        return team.getId();
    }


    @Transactional
    public void doAccept(Long teamId, Long memberId){
        changeState(teamId, memberId, JoinState.OK);
    }

    @Transactional
    public void doBan(Long teamId, Long memberId){
        changeState(teamId, memberId, JoinState.BAN);
    }

    @Transactional
    public void doReject(Long teamId, Long memberId){
        JoinTeam joinTeam = changeState(teamId, memberId, null);
        joinTeamRepository.delete(joinTeam);
    }

    @Transactional
    public JoinTeam changeState(Long teamId, Long memberId, JoinState state){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        if(state == null){
            return joinTeam;
        }
        joinTeam.changeState(state);
        return joinTeam;
    }

    @Transactional
    public void changeDeveloperRole(Long teamId, Long memberId, DeveloperRole role){
        JoinTeam joinTeam = joinTeamService.findByMemberInTeam(memberId, teamId);
        joinTeam.changeDeveloperRole(role);
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
