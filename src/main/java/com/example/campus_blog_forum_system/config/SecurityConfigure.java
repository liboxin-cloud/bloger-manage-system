package com.example.campus_blog_forum_system.config;

import com.example.campus_blog_forum_system.interceptors.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfigure implements WebMvcConfigurer {
    private final JwtInterceptor jwtInterceptor;

    public SecurityConfigure(JwtInterceptor jwtInterceptor) {
        this.jwtInterceptor = jwtInterceptor;
        System.out.println("SecurityConfigure 构造函数被调用");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("开始配置SecurityFilterChain - 允许所有请求");

        http
                .authorizeHttpRequests(authorize -> {
                    System.out.println("配置URL权限规则 - 允许所有请求");
                    authorize.anyRequest().permitAll();
                })
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable);

        System.out.println("SecurityFilterChain配置完成 - 允许所有请求");
        return http.build();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login",
                        "/user/register",
                        "/admin/auth/login",
                        "/admin/auth/info",
                        "/*.html",
                        "/css/**",
                        "/js/**",
                        "/favicon.ico",
                        "/static/**",
                        "/error",
                        "forgot-password",
                        "forgot-password/**");

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("创建 PasswordEncoder");
        return new BCryptPasswordEncoder(BCryptVersion.$2A, 10);
    }
}