package team.project.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import team.project.Entity.Member;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String Home(Model model){
        model.addAttribute("user", "user");
        return "home";
    }

}
