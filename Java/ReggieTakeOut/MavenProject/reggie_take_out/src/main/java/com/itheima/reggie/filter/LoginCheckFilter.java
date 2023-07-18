package com.itheima.reggie.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
@Component
public class LoginCheckFilter implements Filter  {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        log.info("拦截到请求:{}",httpServletRequest.getRequestURI());
        filterChain.doFilter(httpServletRequest,httpServletResponse);
    }
}
