package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    // 步骤1：验证账号
    @PostMapping("/verify-account")
    public Result<Map<String, Object>> verifyAccount(@RequestBody VerifyAccountRequest request) {
        try {
            // 验证验证码（如果有验证码模块）
            // String sessionCaptcha = (String) request.getSession().getAttribute("captcha");
            // if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(request.getCaptcha())) {
            //     return Result.error("验证码错误");
            // }

            // 根据账户类型验证
            boolean isValid = false;
            String email = "";
            String userType = request.getUserType();

            if ("admin".equals(userType)) {
                // 验证管理员账号
                Map<String, Object> result = forgotPasswordService.verifyAdminAccount(request.getUsername());
                isValid = (boolean) result.get("success");
                email = (String) result.get("email");
            } else {
                // 验证普通用户账号
                Map<String, Object> result = forgotPasswordService.verifyUserAccount(request.getUsername());
                isValid = (boolean) result.get("success");
                email = (String) result.get("email");
            }

            if (!isValid) {
                return new Result<>();
            }

            // 生成并发送验证码
            String verificationCode = forgotPasswordService.generateAndSendCode(email, userType);

            // 返回部分脱敏的邮箱
            String maskedEmail = maskEmail(email);

            Map<String, Object> data = new HashMap<>();
            data.put("email", maskedEmail);
            data.put("userType", userType);
            data.put("fullEmail", email);

            return Result.successWithData(data);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

    // 步骤2：验证验证码
    @PostMapping("/verify-code")
    public Result<Void> verifyCode(@RequestBody VerifyCodeRequest request) {
        try {
            boolean isValid = forgotPasswordService.verifyCode(
                    request.getEmail(),
                    request.getCode(),
                    request.getUserType()
            );

            if (!isValid) {
                return Result.error("验证码错误或已过期");
            }

            return Result.success();

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("验证失败: " + e.getMessage());
        }
    }

    // 步骤3：重置密码
    @PostMapping("/reset")
    public Result<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            // 验证密码格式
            if (request.getNewPassword().length() < 5 ||
                    request.getNewPassword().length() > 16 ||
                    request.getNewPassword().contains(" ")) {
                return Result.error("密码必须为5-16位且不能包含空格");
            }

            boolean success = forgotPasswordService.resetPassword(
                    request.getEmail(),
                    request.getNewPassword(),
                    request.getUserType()
            );

            if (!success) {
                return Result.error("密码重置失败");
            }

            return Result.success("密码重置成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("重置失败: " + e.getMessage());
        }
    }

    // 重新发送验证码
    @PostMapping("/resend-code")
    public Result<Void> resendCode(@RequestBody ResendCodeRequest request) {
        try {
            forgotPasswordService.generateAndSendCode(request.getEmail(), request.getUserType());
            return Result.success("验证码已重新发送");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发送失败: " + e.getMessage());
        }
    }

    // 辅助方法：脱敏邮箱
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String localPart = parts[0];
        String domain = parts[1];

        if (localPart.length() <= 2) {
            return localPart.charAt(0) + "***@" + domain;
        }

        return localPart.substring(0, 2) + "***@" + domain;
    }

    // 请求DTO类
    public static class VerifyAccountRequest {
        private String username;
        private String userType;
        private String captcha;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getCaptcha() { return captcha; }
        public void setCaptcha(String captcha) { this.captcha = captcha; }
    }

    public static class VerifyCodeRequest {
        private String email;
        private String code;
        private String userType;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    public static class ResetPasswordRequest {
        private String email;
        private String newPassword;
        private String userType;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }

    public static class ResendCodeRequest {
        private String email;
        private String userType;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
    }
}