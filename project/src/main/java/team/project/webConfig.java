package team.project;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import team.project.Service.ArgumentResolver.LoginCheckArgumentResolver;
import team.project.Service.Interceptor.LoginCheckInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class webConfig implements WebMvcConfigurer {

    private final LoginCheckArgumentResolver loginCheckArgumentResolver;
    private final LoginCheckInterceptor loginCheckInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginCheckArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/*.ico", "/error","/api");
    }
}
