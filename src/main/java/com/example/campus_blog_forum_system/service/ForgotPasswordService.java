package com.example.campus_blog_forum_system.service;

import java.util.Map;

public interface ForgotPasswordService {
    // 验证普通用户账号
    Map<String, Object> verifyUserAccount(String username);

    // 验证管理员账号
    Map<String, Object> verifyAdminAccount(String username);

    // 生成并发送验证码
    String generateAndSendCode(String email, String userType);

    // 验证验证码
    boolean verifyCode(String email, String code, String userType);

    // 重置密码
    boolean resetPassword(String email, String newPassword, String userType);
}