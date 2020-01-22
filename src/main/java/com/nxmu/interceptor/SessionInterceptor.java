package com.nxmu.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import static com.nxmu.common.Route.*;

@Configuration
public class SessionInterceptor extends WebMvcConfigurerAdapter {

    /**
     * 自定义拦截器，添加拦截路径和排除拦截路径
     * addPathPatterns():添加需要拦截的路径
     * excludePathPatterns():添加不需要拦截的路径
     */
    //注册拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/**").excludePathPatterns(
                        USER_VERIFY_CODE, USER_LOGIN, USER_LOGOFF, USER_REGISTER,
                SWAGGER_API_DOCS, SWAGGER_URLS, SWAGGER_RESOURCE_URLS, SWAGGER_V2,
                USER_FORGET_PASSWORD, USER_CHANGE_PASSWORD
                );
    }
}
