package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.mapper.AdminMapper;
import com.example.campus_blog_forum_system.mapper.UserMapper;
import com.example.campus_blog_forum_system.pojo.AdminUser;
import com.example.campus_blog_forum_system.pojo.User;
import com.example.campus_blog_forum_system.service.ForgotPasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ForgotPasswordServiceImpl implements ForgotPasswordService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JavaMailSender mailSender;

    // 使用内存缓存存储验证码
    private final Map<String, CacheEntry> codeCache = new ConcurrentHashMap<>();

    private static final String USER_CODE_PREFIX = "reset:user:";
    private static final String ADMIN_CODE_PREFIX = "reset:admin:";
    private static final long CODE_EXPIRE_TIME = 5 * 60 * 1000; // 5分钟，单位毫秒

    // 缓存条目类
    private static class CacheEntry {
        private final String code;
        private final long expireTime;

        public CacheEntry(String code, long expireTime) {
            this.code = code;
            this.expireTime = expireTime;
        }

        public String getCode() {
            return code;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    @Override
    public Map<String, Object> verifyUserAccount(String username) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        // 通过用户名查找用户
        User user = userMapper.findUserByName(username);
        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            result.put("success", true);
            result.put("email", user.getEmail());
            result.put("userId", user.getId());
        } else {
            // 尝试通过邮箱查找
            User userByEmail = userMapper.findByEmail(username);
            if (userByEmail != null) {
                result.put("success", true);
                result.put("email", userByEmail.getEmail());
                result.put("userId", userByEmail.getId());
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> verifyAdminAccount(String username) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);

        // 通过用户名查找管理员
        AdminUser admin = adminMapper.findByUsername(username);
        if (admin != null && admin.getEmail() != null && !admin.getEmail().isEmpty()) {
            result.put("success", true);
            result.put("email", admin.getEmail());
            result.put("adminId", admin.getId());
        } else {
            // 尝试通过邮箱查找
            AdminUser adminByEmail = adminMapper.findByEmail(username);
            if (adminByEmail != null) {
                result.put("success", true);
                result.put("email", adminByEmail.getEmail());
                result.put("adminId", adminByEmail.getId());
            }
        }

        return result;
    }

    @Override
    public String generateAndSendCode(String email, String userType) {
        // 生成6位随机验证码
        String code = generateVerificationCode();

        // 存储到内存缓存
        String key = ("user".equals(userType) ? USER_CODE_PREFIX : ADMIN_CODE_PREFIX) + email;
        long expireTime = System.currentTimeMillis() + CODE_EXPIRE_TIME;
        codeCache.put(key, new CacheEntry(code, expireTime));

        // 发送邮件
        sendVerificationEmail(email, code, userType);

        // 启动一个清理线程（可选）
        startCleanupTask();

        return code;
    }

    @Override
    public boolean verifyCode(String email, String code, String userType) {
        String key = ("user".equals(userType) ? USER_CODE_PREFIX : ADMIN_CODE_PREFIX) + email;
        CacheEntry entry = codeCache.get(key);

        if (entry != null && !entry.isExpired() && entry.getCode().equals(code)) {
            // 验证成功后删除验证码
            codeCache.remove(key);
            return true;
        }

        // 如果验证码已过期，删除它
        if (entry != null && entry.isExpired()) {
            codeCache.remove(key);
        }

        return false;
    }

    @Override
    public boolean resetPassword(String email, String newPassword, String userType) {
        try {
            String encodedPassword = passwordEncoder.encode(newPassword);

            if ("user".equals(userType)) {
                // 重置普通用户密码
                User user = userMapper.findByEmail(email);
                if (user == null) {
                    user = userMapper.findUserByName(email);
                }
                if (user != null) {
                    user.setPassword(encodedPassword);
                    userMapper.updatePwd(encodedPassword, user.getId().toString());
                    return true;
                }
            } else {
                // 重置管理员密码
                AdminUser admin = adminMapper.findByEmail(email);
                if (admin == null) {
                    admin = adminMapper.findByUsername(email);
                }
                if (admin != null) {
                    admin.setPassword(encodedPassword);
                    adminMapper.updatePasswordById(Math.toIntExact(admin.getId()), encodedPassword);
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // 生成6位验证码
    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    // 发送验证码邮件
    private void sendVerificationEmail(String to, String code, String userType) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("3551839250@qq.com");
            message.setTo(to);
            message.setSubject("校园论坛 - 密码重置验证码");

            String content = String.format(
                    "您好！\n\n" +
                            "您正在进行密码重置操作，您的验证码是：%s\n\n" +
                            "验证码有效期为5分钟，请勿泄露给他人。\n\n" +
                            "如果不是您本人操作，请忽略此邮件。\n\n" +
                            "校园论坛管理系统",
                    code
            );

            message.setText(content);
            mailSender.send(message);
            System.out.println("邮件发送成功到: " + to + "，验证码: " + code);
        } catch (Exception e) {
            e.printStackTrace();
            // 邮件发送失败，但验证码已生成，打印到控制台用于测试
            System.out.println("========== 验证码（邮件发送失败） ==========");
            System.out.println("收件人: " + to);
            System.out.println("验证码: " + code);
            System.out.println("==========================================");
        }
    }

    // 启动清理任务，定期清理过期的验证码
    private void startCleanupTask() {
        // 这里可以启动一个定时任务，但为了简单，我们在每次获取时检查过期
        // 实际项目中可以使用 @Scheduled 注解
    }

    // 可选：添加一个清理过期验证码的方法
    private void cleanupExpiredCodes() {
        codeCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}