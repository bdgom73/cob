package team.project.Service.Interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.RequestContextUtils;
import team.project.CommonConst;
import team.project.Dto.LoginMemberDto;
import team.project.Entity.*;
import team.project.Entity.TeamEntity.Project;
import team.project.Entity.TeamEntity.Team;
import team.project.Entity.TeamEntity.JoinState;
import team.project.Entity.TeamEntity.JoinTeam;
import team.project.Service.JoinTeamService;
import team.project.Service.ProjectService;
import team.project.Service.TeamService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamsUserCheckInterceptor implements HandlerInterceptor {

    private final JoinTeamService joinTeamService;
    private final ProjectService projectService;
    private final TeamService teamService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession(true);
        String requestURI = request.getRequestURI();
        Long uid = (Long) session.getAttribute("UID");
        FlashMapManager fm = RequestContextUtils.getFlashMapManager(request);
        FlashMap flashMap = new FlashMap();
        Map<?,?> pathVariables = (Map<?,?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        log.info("team's authority check start [{}]", requestURI);
        if(uid == null){
            log.info("Not logged in [{}]", requestURI);
            return responseFlashMessage(request, response, fm, flashMap, "로그인 후 이용가능합니다","/login?redirectURI="+ request.getRequestURI());
        }
        String teamId = null;
        String referer = request.getHeader("REFERER");

        log.info("referer = {}",referer);
        try{
            teamId = pathVariables.get("teamId").toString();
        }catch (Exception e){
            String reduri = "/teams";
            if(referer != null){
                reduri = referer;
            }
            return responseFlashMessage(request, response, fm, flashMap, "존재하지않는 주소입니다.", reduri);
        }

        log.info("teamId = {}",teamId);
        request.setAttribute("teamId", teamId);
        if(teamId != null){
            try{
                JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(uid, Long.parseLong(teamId));
                log.info("Check the team's authority [{}]", requestURI);
                request.setAttribute("postJoinTeam", joinTeam);
                if(joinTeam.getJoinState().equals(JoinState.WAITING)){
                    throw new IllegalStateException("아직 가입완료가 되지 않았습니다");
                }else if(joinTeam.getJoinState().equals(JoinState.BAN)){
                    throw new IllegalStateException("접근 권한이 없습니다");
                }
                request.setAttribute(CommonConst.CheckJoinTeam, joinTeam);
            }catch (IllegalStateException e){
                log.info("team's authority fail [{}]", requestURI);
                try{
                    teamService.findById(Long.parseLong(teamId));
                    return responseFlashMessage(request, response, fm, flashMap, "가입 후 접근이 가능합니다","/teams");
                }catch (IllegalStateException ee){
                    return responseFlashMessage(request, response, fm, flashMap, ee.getMessage(),"/teams");
                }
            }catch (NumberFormatException e){
                log.info("team's authority fail [{}]", requestURI);
                return responseFlashMessage(request, response, fm, flashMap, "존재하지 않는 주소입니다.","/teams");
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String teamId = (String) request.getAttribute("teamId");
        if(teamId != null){
            List<Project> projects = projectService.findAllByTeam(Long.parseLong(teamId));
            List<Map<String , Object>> projectNavList = new ArrayList<>();
            List<Map<String , Object>> endProjectNavList = new ArrayList<>();
            projects.stream().forEach(p->{
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("projectId", p.getId());
                resultMap.put("projectName" , p.getTitle());
                if(!p.getProgress().equals(Progress.Maintenance)){
                    projectNavList.add(resultMap);
                }else{
                    endProjectNavList.add(resultMap);
                }
            });

            modelAndView.addObject("projectNav", projectNavList);
            modelAndView.addObject("endProjectNav", endProjectNavList);
        }
        JoinTeam joinTeam = (JoinTeam) request.getAttribute("postJoinTeam");

        if(joinTeam != null){
            Member postMember = joinTeam.getMember();
            Team postTeam = joinTeam.getTeam();
            if(postTeam != null){
                Map<String, Object> resultMap = new HashMap<>();
                resultMap.put("id",postTeam.getMember().getId());
                modelAndView.addObject("teamLeader", resultMap);
            }
            if(postMember != null){
                LoginMemberDto memberDto = new LoginMemberDto(postMember.getId(), postMember.getEmail(), postMember.getName(), postMember.getNickname());
                modelAndView.addObject("loginMember", memberDto);
            }else{
                modelAndView.addObject("loginMember", new LoginMemberDto());
            }
        }


    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(ex != null){
            response.sendRedirect("/teams");
        }
    }



    private boolean responseFlashMessage(HttpServletRequest request, HttpServletResponse response, FlashMapManager fm, FlashMap flashMap, String message, String redirectURI) throws IOException {
        flashMap.put("resultMsg", message);
        if (fm != null) {
            fm.saveOutputFlashMap(flashMap, request, response);
        }
        response.sendRedirect(redirectURI);
        return false;
    }
}
