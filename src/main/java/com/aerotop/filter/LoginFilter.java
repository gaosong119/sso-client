package com.aerotop.filter;

import com.aerotop.constant.AuthConstant;
import com.aerotop.storage.SessionStorage;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @ClassName: LoginFilter
 * @Description: 客户端登录filter
 * @Author: gaosong
 * @Date 2021/1/29 10:08
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = "/*",
        initParams = {@WebInitParam(name = "loginUrl",value = "http://localhost:8080")})
public class LoginFilter implements Filter {
    /**filter配置对象*/
    private FilterConfig config;
    /**
     * @Description: filter初始化函数
     * @Author: gaosong
     * @Date: 2021/1/29 10:26
     * @param filterConfig:filter配置对象
     * @return: void
     **/
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("初始化client端Filter");
        config = filterConfig;
    }
    /**
     * @Description: filter执行函数
     * @Author: gaosong
     * @Date: 2021/1/29 10:35
     * @param servletRequest: 请求对象
     * @param servletResponse: 响应对象
     * @param filterChain: filter链
     * @return: void
     **/
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws
            IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession();
        // 已经登录,放行
        if (session.getAttribute(AuthConstant.IS_LOGIN) != null) {
            filterChain.doFilter(request, response);
            return;
        }
        // 从认证中心回跳的带有token的请求，有效则放行
        String token = request.getParameter(AuthConstant.TOKEN);
        if (token != null) {
            session.setAttribute(AuthConstant.IS_LOGIN, true);
            session.setAttribute(AuthConstant.TOKEN, token);
            // 存储,用于注销
            SessionStorage.INSTANCE.set(token, session);
            filterChain.doFilter(request, response);
            return;
        }
        // 重定向至登录页面，并附带当前请求地址
        response.sendRedirect(config.getInitParameter(AuthConstant.LOGIN_URL) + "?" + AuthConstant.CLIENT_URL + "="
                + request.getRequestURL());
    }
    /**
     * @Description: filter退出时执行函数
     * @Author: gaosong
     * @Date: 2021/1/29 10:49
     * @return: void
     **/
    @Override
    public void destroy() {

    }
}
