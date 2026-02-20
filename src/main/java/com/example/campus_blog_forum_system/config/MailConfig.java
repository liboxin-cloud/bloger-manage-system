package com.example.campus_blog_forum_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // 直接硬编码测试（先测试能否工作）
        mailSender.setHost("smtp.qq.com");
        mailSender.setPort(587);
        mailSender.setUsername("3551839250@qq.com");
        mailSender.setPassword("wzyjapbffcrzchic");
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.qq.com");
        props.put("mail.debug", "true");

        return mailSender;
    }
}