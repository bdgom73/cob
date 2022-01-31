package team.project.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import team.project.Dto.JoinMemberDto;
import team.project.Service.MemberService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.websocket.server.PathParam;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;

    @GetMapping("/")
    public String Home(Model model){
        return "/home";
    }

    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("loginFormData", new LoginForm());
        return "home/loginForm";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginFormData") LoginForm form, BindingResult bindingResult,
                        HttpServletRequest request, @PathParam("redirectURI") String redirectURI){

        if(bindingResult.hasErrors()){
            return "home/loginForm";
        }

        try{
            Long memberId  = memberService.login(form.getEmail(), form.getPassword());
            log.info("login? {}", memberId);
            HttpSession session = request.getSession();
            session.setAttribute("UID", memberId);
            if(StringUtils.hasText(redirectURI)){
                return "redirect:"+ redirectURI;
            }
            return "redirect:/";
        }catch (IllegalStateException e){
            bindingResult.reject("loginFail", e.getMessage());
            return "home/loginForm";
        }catch (UsernameNotFoundException e){
            bindingResult.reject("loginFail", e.getMessage());
            return "home/loginForm";
        }

    }

    @GetMapping("/join")
    public String joinForm(Model model){
        model.addAttribute("joinFormData", new JoinMemberForm());
        return "home/joinForm";

    }

    @PostMapping("/join")
    public String join(@Validated @ModelAttribute("joinFormData") JoinMemberForm form, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            log.info("bindingResult = {}", bindingResult);
            return "home/joinForm";
        }

        try{
            JoinMemberDto joinMemberDto = new JoinMemberDto(form.getEmail(), form.getPassword1(), form.getPassword2(), form.getName(), form.getNickname());
            memberService.join(joinMemberDto);
        }catch (IllegalStateException e){
            log.info("fail : {}",e.getMessage());
            bindingResult.reject("joinFail", e.getMessage());
            return "home/joinForm";
        }

        return "redirect:/login";
    }


    @Data
    class JoinMemberForm {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        @Size(min = 9)
        private String password1;
        @NotBlank
        @Size(min = 9)
        private String password2;
        @NotBlank
        private String name;
        @NotBlank
        private String nickname;
    }

    @Data
    class LoginForm{
        @NotBlank
        private String email;
        @NotBlank
        private String password;
    }

}
