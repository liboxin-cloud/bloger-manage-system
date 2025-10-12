package com.example.campus_blog_forum_system;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;

public class PasswordTest
{

//    初始化加密后的哈希: $2a$10$ORvjxxKCTlyFJHLIeVOUYORdVUGRBDk5ohTaFKZXPfRxQopQnplEW
//    $2a$10$d6DZgvLsA2m.iI5XHEf5r.J3g2QqLtbLctWyWXi4M3PWjq/kHLR2W
    public static void main(String[] args)
    {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(BCryptVersion.$2A, 10);

        // 数据库中存储的哈希
        String dbHash = "$2a$10$q/LeiCQydaMB8Df5/uvrzeFLX3PTt0M3JTp/qU1LmBzX7IjlnlbMy";
        // 输入的明文密码
        String rawPassword = "admin";

        // 验证是否匹配
        boolean isMatch = encoder.matches(rawPassword, dbHash);
        System.out.println("明文与哈希是否匹配: " + isMatch); // 预期应为true，若为false则哈希无效

        // 额外生成一个新的哈希（用于对比格式和规则）
        String newHash = encoder.encode(rawPassword);
        System.out.println("新生成的哈希: " + newHash);
        System.out.println("新哈希与明文是否匹配: " + encoder.matches(rawPassword, newHash)); // 应为true
    }
}