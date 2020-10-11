package com.nxmu.interceptor;

import com.nxmu.exception.ResultException;
import com.nxmu.model.vo.LoginUserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.nxmu.common.Constant.USER_TOKEN;

@Component
@Slf4j
public class UserInterceptor implements HandlerInterceptor {
    /*
     * 进入controller层之前拦截请求
     * 返回值：表示是否将当前的请求拦截下来  false：拦截请求，请求别终止。true：请求不被拦截，继续执行
     * Object obj:表示被拦的请求的目标对象（controller中方法）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        LoginUserVO user = (LoginUserVO) request.getSession().getAttribute("user");
        Cookie[] cookies = request.getCookies();

        if (user == null || cookies == null || cookies.length <= 0){
            //response.sendRedirect(request.getContextPath()+"/user/toIndex");//拦截后跳转的方法
            //return false;
            throw new ResultException("请先登录！");
        }

        String userToken = "";
        for (Cookie cookie : cookies) {
            if (USER_TOKEN.equals(cookie.getName())) {
                userToken = cookie.getValue();
            }
        }

        if (!user.getToken().equals(userToken)) {
            throw new ResultException("token不可用，请先登录！");
        }

        return true;
    }

    /*
     * 处理请求完成后视图渲染之前的处理操作
     * 通过ModelAndView参数改变显示的视图，或发往视图的方法
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    /*
     * 视图渲染之后的操作
     */
    @Override
    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3) throws Exception {

    }
}
