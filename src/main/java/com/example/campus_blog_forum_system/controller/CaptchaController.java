// src/main/java/com/example/campus_blog_forum_system/controller/CaptchaController.java
package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.utils.CaptchaUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    /**
     * 获取验证码
     * @param session HTTP会话
     * @return 验证码图片和相关信息
     */
    @GetMapping("/generate")
    public Map<String, Object> generateCaptcha(HttpSession session) {
        CaptchaUtil.CaptchaInfo captchaInfo = CaptchaUtil.generateCaptcha();

        // 将验证码存储在session中
        session.setAttribute("captcha", captchaInfo.getCode());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("image", captchaInfo.getImageBase64());
        response.put("message", "验证码生成成功");

        return response;
    }
}
