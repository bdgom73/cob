package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import team.project.Dto.JoinMemberDto;
import team.project.Entity.Member;
import team.project.Entity.RoleType;
import team.project.Repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtToken jwtToken;

    public Long join(JoinMemberDto memberDto){
        memberRepository.findByEmail(memberDto.getEmail()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 계정입니다");
        });
        checkedPassword(memberDto.getPassword1(), memberDto.getPassword2());
        Member member = new Member(
                memberDto.getEmail(), memberDto.getPassword1(), memberDto.getName(), memberDto.getNickname(), RoleType.USER
        );
        memberRepository.save(member);
        return member.getId();
    }

    public Long login(String email, String password){
        Member member = findByEmail(email);
        checkedPassword(member.getPassword(), password);
        return member.getId();
    }

    public Member findById(Long id){
        return memberRepository.findById(id).orElseThrow();
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email).orElseThrow(()->{
            throw new UsernameNotFoundException("존재하지 않는 유저입니다");
        });
    }

    private void checkedPassword(String password1 , String password2){
        if(!password1.equals(password2))
            throw new IllegalStateException("패스워드가 일치하지 않습니다.");
    }
}
