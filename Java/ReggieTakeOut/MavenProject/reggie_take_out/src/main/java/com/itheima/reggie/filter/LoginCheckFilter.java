package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
// 或者在Application里加一个ServletComponentScan,和此处加Component效果一样。作用是扫描WebFilter注解
@Component
public class LoginCheckFilter implements Filter  {
    // 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1.获取URI
        String requestURI = request.getRequestURI();

        // 2.判断是否需要处理
        System.out.println("22222");
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };
        boolean check= checkURI(urls, requestURI);

        // 3.若不处理则放行
        System.out.println("33333");
        if (check){
            filterChain.doFilter(request,response);
            return;
        }

        // 4.判断是否登陆,若登陆则放行
        // 注意，这个只是演示，开发中要校验session是否合法，而不是简单判断是否为null
        System.out.println("44444");
        if(request.getSession().getAttribute("employee")!=null){
            filterChain.doFilter(request,response);
            return;
        }

        // 5.未登陆则返回未登陆结果，通过输出流向客户端页面响应数据
        System.out.println("55555");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次是否放行
     * @param requestURI
     * @return
     */
    public boolean checkURI(String[] urls, String requestURI){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }


}
