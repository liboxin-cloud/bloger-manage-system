package com.example.campus_blog_forum_system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.example.campus_blog_forum_system.config.MailConfig;

@SpringBootTest
public class MailTest {

    @Autowired
    private JavaMailSender mailSender;

    @Test
    public void testSendMail() {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("3551839250@qq.com");
            message.setTo("3551839250@qq.com"); // 先发给自己测试
            message.setSubject("测试邮件");
            message.setText("这是一封来自校园论坛的测试邮件");

            mailSender.send(message);
            System.out.println("邮件发送成功！");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("邮件发送失败：" + e.getMessage());
        }
    }
}