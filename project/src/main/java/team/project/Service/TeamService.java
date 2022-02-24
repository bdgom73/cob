package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.project.Entity.*;
import team.project.Entity.TeamEntity.*;
import team.project.Repository.*;
import team.project.Repository.Team.CalendarRepository;
import team.project.Repository.Team.JoinTeamRepository;
import team.project.Repository.Team.OneLineRepository;
import team.project.Repository.Team.TeamRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final JoinTeamRepository joinTeamRepository;
    private final JoinTeamService joinTeamService;
    private final OneLineRepository oneLineRepository;
    private final CalendarRepository calendarRepository;

    @Transactional
    public Long createTeam(String teamName, String introduction, Long memberId){
        Member member = memberRepository.findById(memberId).orElseThrow(()->{
            throw new UsernameNotFoundException("존재하지 않는 유저입니다");
        });
        Optional<Team> findTeam = teamRepository.findByName(teamName);
        if(findTeam.isPresent()){
            throw new IllegalStateException("이미 존재하는 팀이름입니다");
        }
        long count = teamRepository.countByMemberId(memberId);
        if(count >= 5){
            throw new IllegalStateException("더 이상 팀 생성이 불가능합니다");
        }
        Team team = Team.createTeam(teamName, introduction, member);
        teamRepository.save(team);
        return team.getId();
    }

    @Transactional
    public Long applyTeam(Long teamId, Long memberId){
        Optional<JoinTeam> findJoinTeam = joinTeamRepository.findMemberAndTeamByMemberAndTeam(memberId, teamId);
        if(findJoinTeam.isPresent()){
            JoinTeam joinTeam = findJoinTeam.get();
            JoinState joinState = joinTeam.getJoinState();
            if(joinState.equals(JoinState.OK)){
                throw new IllegalStateException("이미 팀에 가입되어있습니다");
            }else if(joinState.equals(JoinState.WAITING)){
                throw new IllegalStateException("이미 팀에 신청 했습니다");
            }else if(joinState.equals(JoinState.BAN)){
                throw new IllegalStateException("신청이 불가능한 회원입니다");
            }
             throw new IllegalStateException("알 수 없는 이유로 신청에 실패했습니다");
        }
        Team team = findById(teamId);
        Member member = memberRepository.findById(memberId).orElseThrow(() -> {
            throw new IllegalStateException("회원을 찾을 수 없습니다");
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

    @Transactional
    public void deleteTeam(Long teamId){
        List<Calendar> calendars = calendarRepository.findByTeamId(teamId);
        List<OneLine> oneLines = oneLineRepository.findByTeamId(teamId);
        calendarRepository.deleteAll(calendars);
        oneLineRepository.deleteAll(oneLines);
        teamRepository.deleteById(teamId);
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
