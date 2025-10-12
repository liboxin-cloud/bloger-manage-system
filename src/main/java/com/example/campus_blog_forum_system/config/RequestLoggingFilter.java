package com.example.campus_blog_forum_system.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        System.out.println("=================================================");
        System.out.println("收到请求: " + httpRequest.getMethod() + " " + httpRequest.getRequestURI());
        System.out.println("请求参数: " + httpRequest.getQueryString());
        System.out.println("远程地址: " + httpRequest.getRemoteAddr());
        System.out.println("User-Agent: " + httpRequest.getHeader("User-Agent"));
        System.out.println("Authorization: " + httpRequest.getHeader("Authorization"));
        System.out.println("=================================================");

        long startTime = System.currentTimeMillis();

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("请求处理完成: " + httpRequest.getRequestURI() +
                    ", 状态码: " + httpResponse.getStatus() +
                    ", 耗时: " + duration + "ms");
            System.out.println("-------------------------------------------------");
        }
    }
}