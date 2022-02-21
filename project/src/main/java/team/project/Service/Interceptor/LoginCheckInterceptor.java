package team.project.Service.Interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import team.project.Dto.LoginMemberDto;
import team.project.Entity.Member;
import team.project.Repository.MemberRepository;
import team.project.Service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class LoginCheckInterceptor implements HandlerInterceptor {

    private final MemberRepository memberRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String contentType = request.getContentType();
        log.info("LoginCheckURI = [{}][{}][{}]", requestURI, handler, contentType);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        HttpSession session = request.getSession();
        String contentType = request.getContentType();
        Object sessionObject = session.getAttribute("UID");
        log.info("uid = {}",sessionObject);
        if(sessionObject != null){
            Long uid = (Long) sessionObject;
            Optional<Member> findMember = memberRepository.findById(uid);

            if(!Objects.equals(contentType, "application/json")){
                if(findMember.isPresent()){
                    Member member = findMember.get();
                    LoginMemberDto memberDto = new LoginMemberDto(member.getId(), member.getEmail(), member.getName(), member.getNickname());
                    log.info("login Member = {}", memberDto);
                    modelAndView.addObject("loginMember", memberDto);
                }else{
                    session.invalidate();
                    modelAndView.addObject("loginMember", new LoginMemberDto());
                }
            }


        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
