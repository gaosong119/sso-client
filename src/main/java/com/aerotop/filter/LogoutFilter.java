package com.aerotop.filter;

import com.aerotop.constant.AuthConstant;
import com.aerotop.storage.SessionStorage;
import com.aerotop.util.HttpUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: LogoutFilter
 * @Description: 客户端注销filter
 * @Author: gaosong
 * @Date 2021/1/29 10:50
 */
@WebFilter(filterName = "LogoutFilter", urlPatterns = "/logout",
        initParams = {@WebInitParam(name = "logoutUrl",value = "http://localhost:8080/logout")})
public class LogoutFilter implements Filter {
    /**filter配置对象*/
    private FilterConfig config;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("进入注销filter");
        config = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        //获取注销URL
        String logoutUrl = config.getInitParameter(AuthConstant.LOGOUT_URL);
        //获取待注销session
        String token = (String) session.getAttribute(AuthConstant.TOKEN);
        // 主动注销,即子系统提供的注销请求
        String logOutPath = "/logout";
        if (logOutPath.equals(request.getRequestURI())) {
            // 向认证中心发送注销请求
            Map<String, String> params = new HashMap<>(16);
            params.put(AuthConstant.LOGOUT_REQUEST, token);
            HttpUtil.postDestroy(logoutUrl, params);
            // 注销后重定向
            response.sendRedirect("/test");
            // 注销本地会话
            session = SessionStorage.INSTANCE.destroyToken(token);
            if (session != null) {
                session.invalidate();
            }
            return;
        }
        // 被动注销,即从认证中心发送的注销请求
        token = request.getParameter(AuthConstant.LOGOUT_REQUEST);
        if (token != null && !"".equals(token)) {
            session = SessionStorage.INSTANCE.destroyToken(token);
            if (session != null) {
                session.invalidate();
            }
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
