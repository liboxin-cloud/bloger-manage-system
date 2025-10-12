package com.example.campus_blog_forum_system.controller.admin;

import com.example.campus_blog_forum_system.mapper.AdminMapper;
import com.example.campus_blog_forum_system.pojo.AdminUser;
import com.example.campus_blog_forum_system.service.AdminService;
import com.example.campus_blog_forum_system.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.core.StandardContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminMapper adminMapper;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request, HttpSession session) {
        try{
            //===============================验证码模块==========================================

//            try {
//                String sessionCaptcha = (String) session.getAttribute("captcha");
//                if (sessionCaptcha == null ||
//                        request.getCaptcha() == null ||
//                        !sessionCaptcha.equalsIgnoreCase(request.getCaptcha())) {
//                    return ResponseEntity.status(401).body(Map.of("error", "验证码错误"));
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//
//            // 验证成功后清除验证码，防止重复使用
//            session.removeAttribute("captcha");


            //===============================验证码模块==========================================
            // 1. 查询用户
            AdminUser admin = adminService.findByUsername(request.getUsername());
            if (admin == null) {
                return ResponseEntity.status(401).body(Map.of("error", "用户不存在"));
            }

            //检查账户是否锁定
            if (admin.getStatus() == 2) {
                if (admin.getLastFailedLoginTime() != null) {
                    long lockoutDuration = 5 * 60 * 1000;
                    if (System.currentTimeMillis() - admin.getLastFailedLoginTime().getTime() < lockoutDuration) {
                        long remainingTime = lockoutDuration - (System.currentTimeMillis() - admin.getLastFailedLoginTime().getTime());
                        return ResponseEntity.status(401).body(Map.of("error", "账户被锁定，请" + remainingTime / 1000 + "秒后重试"));
                    }
                } else {
                    admin.setLoginAttempts(0);
                    admin.setStatus(1);
                    adminService.updateAdmin(admin);
                }
            }

            // 2. 验证密码
            if (!passwordEncoder.matches(request.getPassword(), admin.getPassword())) {
                int attempts = admin.getLoginAttempts() == null ? 1 : admin.getLoginAttempts() + 1;
                admin.setLoginAttempts(attempts);

                if (attempts >= 5) {
                    admin.setStatus(2);
                }

                admin.setLastFailedLoginTime(new Date());
                adminService.updateAdmin(admin);

                return ResponseEntity.status(401).body(Map.of("error", "密码错误"));
            }

            // 3. 检查账号状态
            if (admin.getStatus() != 1) {
                return admin.getStatus() == 0 ?
                        ResponseEntity.status(401).body(Map.of("error", "账号已被禁用")) :
                        ResponseEntity.status(401).body(Map.of("error", "账号已被锁定"));
            }
            //登录成功，重置失败次数
            admin.setLoginAttempts(0);
            admin.setLastFailedLoginTime(null);
            adminService.updateAdmin(admin);


            // 4. 准备claims
            Map<String, String> claims = new HashMap<>();
            claims.put("username", admin.getUsername());
            // 存储角色ID而不是角色名称
            claims.put("role", String.valueOf(admin.getRole()));

            // 5. 生成Token
            String token = JwtUtil.genToken(claims);

            // 6. 返回包含token和用户信息的响应
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", admin.getUsername());
            // 返回角色ID
            response.put("role", admin.getRole());

            System.out.println("管理员登录成功，生成token: " + token);
            System.out.println("用户角色: " + admin.getRole());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "服务器内部错误"));
        }
    }
    // 登录请求的 DTO
    public static class LoginRequest
    {
        private String username;
        private String password;
        private String captcha;
        
        // Getter 和 Setter
        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getCaptcha() {
            return captcha;
        }

        public void setCaptcha(String captcha) {
            this.captcha = captcha;
        }
    }
    // 新增：获取当前管理员信息接口
// 确保AuthController中的info接口允许普通管理员访问
    @GetMapping("/info")
    public ResponseEntity<?> getAdminInfo(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            System.out.println("接收到的原始token: " + token);

            if(token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("处理后的token: " + token);
            }

            String username = JwtUtil.getUsernameFromToken(token);
            String role = JwtUtil.getRoleFromToken(token); // 修改为String类型

            Map<String, Object> info = new HashMap<>();
            info.put("username", username);
            info.put("role", role); // role本身就是字符串

            return ResponseEntity.ok(info);
        } catch (Exception e) {
            System.out.println("Token解析异常: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(403).body("Token验证失败");
        }
    }
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            // 验证Token有效性
            if (!jwtUtil.validateToken(token.substring(7))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // 检查用户状态
            String username = jwtUtil.getUsernameFromToken(token.substring(7));
            AdminUser admin = adminService.findByUsername(username);
            if (admin == null || admin.getStatus() != 1) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("账号无效或已被禁用");
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @GetMapping("/refresh")
    public ResponseEntity<String> refreshToken(HttpServletRequest request) {
        String oldToken = request.getHeader("Authorization");
        if (oldToken == null || !oldToken.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("无效的Token格式");
        }

        try {
            String newToken = jwtUtil.refreshToken(oldToken.substring(7));
            return ResponseEntity.ok(newToken);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token刷新失败");
        }
    }



}