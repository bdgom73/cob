package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import team.project.Dto.JoinMemberDto;
import team.project.Entity.Member;
import team.project.Entity.RoleType;
import team.project.Repository.MemberRepository;

import javax.annotation.PostConstruct;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    public Long join(JoinMemberDto memberDto){
        memberRepository.findByEmail(memberDto.getEmail()).ifPresent(m->{
            throw new IllegalStateException("이미 존재하는 계정입니다");
        });
        checkedPassword(memberDto.getPassword1(), memberDto.getPassword2());
        Member member = new Member(
                memberDto.getEmail(), passwordEncoder.encode(memberDto.getPassword1()), memberDto.getName(), memberDto.getNickname(), RoleType.USER
        );
        memberRepository.save(member);
        return member.getId();
    }

    public Long login(String email, String password){
        Member member = findByEmail(email);
        matchesPassword(password, member.getPassword());
        return member.getId();
    }

    private void matchesPassword(String inputPassword, String password) {
        boolean matches = passwordEncoder.matches(inputPassword, password);
        if(!matches){
            throw new IllegalStateException("패스워드가 일치하지 않습니다.");
        }
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
