package com.example.campus_blog_forum_system.config;

import com.example.campus_blog_forum_system.interceptors.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/admin/auth/login",
                        "/user/login",
                        "/user/register",
                        "/error",
                        "/static/**",
                        "/login",
                        "/admin",
                        "/index");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 保留静态资源映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 使用转发而不是重定向，避免URL显示实际文件路径
        registry.addViewController("/login").setViewName("forward:/static/login.html");
        registry.addViewController("/admin").setViewName("forward:/static/admin.html");
        registry.addViewController("/index").setViewName("forward:/static/index.html");
    }
}
