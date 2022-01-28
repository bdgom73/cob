package team.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import team.project.Entity.Member;
import team.project.Repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final JwtToken jwtToken;

    public void join(){
        // TODO join member
    }

    public String login(String email, String password){
        Member member = findByEmail(email);
        checkedPassword(member.getPassword(), password);
        return jwtToken.createToken(member.getId());
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
        if(password1.equals(password2))
            throw new IllegalStateException("패스워드가 올바르지 않습니다");
    }
}
