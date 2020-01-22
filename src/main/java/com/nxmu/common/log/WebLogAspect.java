package com.nxmu.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author zhanglily
 * @date 2020/01/20
 */
@Aspect
@Component
@Order(5)
@Slf4j
public class WebLogAspect {
    private static final Integer HEADER_TYPE = 1;
    private static final Integer PARAM_TYPE = 2;
    private ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("execution(public * com.nxmu.controller..*.*(..)) && within(com.nxmu.controller..*)")
    public void webLog() {
    }

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        startTime.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录请求内容
        log.info("request url: {} headers: {}; params: {}", request.getRequestURL().toString(), getParams(request, request.getHeaderNames(), HEADER_TYPE), getParams(request, request.getParameterNames(), PARAM_TYPE));

        log.info("aspect method: {} ", new StringBuilder(joinPoint.getSignature().getDeclaringTypeName()).append(".").append(joinPoint.getSignature().getName()).toString());

    }

    @AfterReturning(pointcut = "webLog()")
    public void doAfterReturning() {
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 记录请求内容
        log.info("request url : {} headers: {}; params: {}; spend time: {} millis", request.getRequestURL().toString(), getParams(request, request.getHeaderNames(), HEADER_TYPE), getParams(request, request.getParameterNames(), PARAM_TYPE)
                , (System.currentTimeMillis() - startTime.get()));
    }


    /**
     * 获取参数
     *
     * @param request
     * @return
     */
    private String getParams(HttpServletRequest request, Enumeration<String> paramNames, Integer paramType) {
        StringBuffer builder = new StringBuffer("");
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValues = HEADER_TYPE.equals(paramType) ? request.getHeader(paramName) : PARAM_TYPE.equals(paramType) ? request.getParameter(paramName) : "";
            builder.append(paramName).append(" = ").append(paramValues).append(" , \n ");
        }
        return builder.toString();
    }
}
