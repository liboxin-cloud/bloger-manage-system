package com.example.campus_blog_forum_system;

import java.io.*;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class BigEventApplicationTests
{

    @Test
    void testPasswordEncoding() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 用户输入密码为 "admin"
        String plainPassword = "admin";
        String hashedPassword = "$2a$10$BNtnWjPUDnZO5Zf/GFd1q.1zV1bQ/0em9D4Hvb4ZVEU.7Fny7by9S";

        boolean matches = encoder.matches(plainPassword, hashedPassword);
        System.out.println("密码匹配结果: " + matches);

        // 如果需要生成新的加密密码
        String newEncodedPassword = encoder.encode("admin");
        System.out.println("新生成的加密密码: " + newEncodedPassword);
    }
}
