package com.example.campus_blog_forum_system.interceptors;

import com.example.campus_blog_forum_system.utils.JwtUtil;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        System.out.println("JwtInterceptor拦截请求: " + request.getRequestURI());
        System.out.println("请求方法: " + request.getMethod());

        String requestURI = request.getRequestURI();

        // 检查是否是.html结尾的请求，如果是则直接拒绝
        if (requestURI.endsWith(".html")) {
            System.out.println("JwtInterceptor拒绝.html请求: " + requestURI);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "禁止直接访问HTML文件");
            return false;
        }

        // 1. 放行不需要认证的路径（如静态资源、登录接口）
        if (isPathToSkip(request.getRequestURI())) {
            System.out.println("JwtInterceptor放行路径: " + request.getRequestURI());
            return true;
        }

        // 从请求头中获取token
        String token = request.getHeader("Authorization");
        System.out.println("从请求头获取的Authorization: " + token);

        // 更健壮的token前缀处理（处理大小写不一致的情况）
        if (token != null) {
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7); // 去掉"Bearer "前缀（无论大小写）
                System.out.println("处理后的token: " + token);
            }
        }

        // 开始验证token
        try {
            if (token != null && !token.isEmpty()) {
                Map<String, Object> claims = JwtUtil.parseToken(token);
                System.out.println("JwtInterceptor解析到claims: " + claims);

                // 把业务逻辑存储在ThreadLocal中
                ThreadLocalUtil.set(claims);

                // 设置Spring Security上下文
                setupSpringSecurityContext(claims);
            } else {
                System.out.println("JwtInterceptor: 没有提供token");
                response.setStatus(401);
                return false;
            }
            System.out.println("JwtInterceptor处理完成，放行请求");
            return true;
        } catch (Exception e) {
            System.out.println("JwtInterceptor令牌解析失败: " + e.getMessage());
            e.printStackTrace();
            // 验证不成功，返回状态码401
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        // 删除对象防止内存泄露
        ThreadLocalUtil.remove();
        // 清理SecurityContext
        SecurityContextHolder.clearContext();
    }

    /**
     * 判断请求路径是否需要跳过 JWT 验证
     * @param requestURI 请求路径
     * @return 如果需要跳过返回 true，否则返回 false
     */
    private boolean isPathToSkip(String requestURI) {
        // 定义不需要验证的路径列表
        String[] skipPaths = {
                "/user/login",
                "/user/register",
                "/admin/auth/login",
                "/admin/auth/info",
                "/error",
                "/favicon.ico",
                "/login",
                "/admin",
                "/index",
                "/captcha/generate",
                "/register",
                "/index/my-article",
                "/index/publish",
                "/my-article",
                "/homepage",
                "/index/homepage",
                "/forgot-password",
                "/forgot-password/verify-account",
                "/forgot-password/reset",
                "/forgot-password/verify-code",
                "/forgot-password/resend-code",
                "/index/user-info"
        };
        // 检查是否是静态资源（但排除.html文件）
        if (requestURI.endsWith(".html")) {
            // 不跳过.html文件，让拦截器处理拒绝访问
            return false;
        }
        // 检查是否是静态资源
        if (
                requestURI.startsWith("/css/") ||
                requestURI.startsWith("/js/") ||
                requestURI.startsWith("/static/")) {
            return false;
        }

        // 检查请求路径是否匹配任何不需要验证的路径
        for (String pattern : skipPaths) {
            if (requestURI.equals(pattern)) {
                return true;
            }
        }

        return false;
    }

    // 设置 Spring Security 上下文

    // 设置 Spring Security 上下文
    // 设置 Spring Security 上下文
    // 设置 Spring Security 上下文
    private void setupSpringSecurityContext(Map<String, Object> claims) {
        String username = (String) claims.get("username");
        String role = (String) claims.get("role");

        System.out.println("准备设置Spring Security上下文:");
        System.out.println("用户名: " + username);
        System.out.println("角色: " + role);

        // 创建权限列表 - 与SecurityConfigure中配置的权限名称保持一致
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        // 根据角色ID设置权限
        if (role != null) {
            try {
                int roleId = Integer.parseInt(role);
                System.out.println("解析角色ID: " + roleId);
                if (roleId == 1) { // 假设1是超级管理员
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    System.out.println("添加权限: ROLE_ADMIN");
                } else { // 其他情况也给管理员权限
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                    System.out.println("添加权限: ROLE_ADMIN");
                }
            } catch (NumberFormatException e) {
                System.out.println("角色ID解析失败: " + e.getMessage());
                // 如果无法解析为数字，默认给管理员权限
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                System.out.println("添加默认权限: ROLE_ADMIN");
            }
        } else {
            // 默认给管理员权限
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            System.out.println("添加默认权限: ROLE_ADMIN");
        }

        // 创建认证对象
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(username, null, authorities);

        // 设置到 SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(auth);

        System.out.println("JwtInterceptor已设置权限: " + authorities);

        // 验证设置是否成功
        Authentication authResult = SecurityContextHolder.getContext().getAuthentication();
        if (authResult != null) {
            System.out.println("验证设置结果 - 用户: " + authResult.getPrincipal());
            System.out.println("验证设置结果 - 权限: " + authResult.getAuthorities());
        }
    }
}