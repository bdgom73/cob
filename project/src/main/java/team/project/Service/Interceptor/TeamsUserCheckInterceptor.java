package team.project.Service.Interceptor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.support.RequestContextUtils;
import team.project.CommonConst;
import team.project.Entity.JoinState;
import team.project.Entity.JoinTeam;
import team.project.Repository.TeamRepository;
import team.project.Service.JoinTeamService;
import team.project.Service.TeamService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamsUserCheckInterceptor implements HandlerInterceptor {

    private final TeamService teamService;
    private final JoinTeamService joinTeamService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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

        String teamId = (String) pathVariables.get("teamId");
        log.info("teamId = {}",teamId);
        if(teamId != null){
            try{
                JoinTeam joinTeam = joinTeamService.findMemberAndTeamByMemberInTeam(uid, Long.parseLong(teamId));
                log.info("Check the team's authority [{}]", requestURI);
                if(!joinTeam.getJoinState().equals(JoinState.OK)){
                    throw new IllegalStateException();
                }
                request.setAttribute(CommonConst.CheckJoinTeam, joinTeam);
            }catch (IllegalStateException e){
                log.info("team's authority fail [{}]", requestURI);
                return responseFlashMessage(request, response, fm, flashMap, "팀 접근 권한이 없습니다.","/teams");
            }
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

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
