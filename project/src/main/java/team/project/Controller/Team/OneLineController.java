package team.project.Controller.Team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import team.project.CommonConst;
import team.project.Controller.Result;
import team.project.Entity.Member;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Entity.TeamEntity.OneLine;
import team.project.Entity.TeamEntity.Team;
import team.project.Service.OneLineService;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OneLineController {

    private final OneLineService oneLineService;

    @GetMapping("/teams/{teamId}/oneday")
    public String oneDay(Model model, @PathVariable("teamId") Long teamId, @RequestParam(value = "date",defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date){
        List<OneLineResponse> resultList = oneLineService.findByDate(teamId, date).stream().map(o -> {
            Member m = o.getMember();
            return new OneLineResponse(o.getId(), o.getText(), o.getDate(), m.getId(), m.getNickname(), m.getName(), o.getCreatedDate(), o.getModifiedDate());
        }).collect(Collectors.toList());
        model.addAttribute("oneLines", resultList);
        model.addAttribute("oneLineForm", new OneDayForm());
        return "team/fragment/teamOneLine";
    }

    @GetMapping("/api/teams/{teamId}/oneday")
    @ResponseBody
    public Result<List<OneLineResponse>> apiOneDay(@PathVariable("teamId") Long teamId, @RequestParam(value = "date",defaultValue = "#{T(java.time.LocalDate).now()}") LocalDate date){
        List<OneLineResponse> resultList = oneLineService.findByDate(teamId, date).stream().map(o -> {
            Member m = o.getMember();
            return new OneLineResponse(o.getId(), o.getText(), o.getDate(), m.getId(), m.getNickname(), m.getName(), o.getCreatedDate(), o.getModifiedDate());
        }).collect(Collectors.toList());
        return new Result<>(resultList);
    }

    @PostMapping("/teams/{teamId}/oneday/create")
    public String createOneDay(@PathVariable("teamId") Long teamId, @Validated @ModelAttribute("oneLineForm") OneDayForm form, BindingResult bindingResult, @RequestAttribute(CommonConst.CheckJoinTeam) JoinTeam joinTeam){

        if(bindingResult.hasErrors()){
            return "redirect:/teams/"+teamId+"/oneday";
        }
        oneLineService.writeOneLine(form.getText(),joinTeam.getTeam(), joinTeam.getMember() );
        return "redirect:/teams/"+teamId+"/oneday";
    }

    @Data
    class OneDayForm{
        @NotBlank
        private String text;
    }

    @Data
    @AllArgsConstructor
    class OneLineResponse{
        private Long oneLineId;
        private String text;
        private LocalDate date;
        private Long memberId;
        private String memberNickname;
        private String memberName;
        private LocalDateTime createDate;
        private LocalDateTime modifiedDate;
    }
}
