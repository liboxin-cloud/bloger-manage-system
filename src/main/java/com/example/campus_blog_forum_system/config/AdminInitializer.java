package com.example.campus_blog_forum_system.config;

import com.example.campus_blog_forum_system.config.properties.AdminProperties;
import com.example.campus_blog_forum_system.pojo.AdminUser;
import com.example.campus_blog_forum_system.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final AdminService adminService;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Autowired
    public AdminInitializer(AdminService adminService,
                            PasswordEncoder passwordEncoder,
                            AdminProperties adminProperties) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
        this.adminProperties = adminProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminProperties.getAuth().isEnabled() &&
                adminService.findAllAdmins(1, 1).getTotal() == 0) {

            AdminUser admin = new AdminUser();
            admin.setUsername(adminProperties.getAuth().getDefaultUsername());
            //admin.setPassword(passwordEncoder.encode(adminProperties.getAuth().getDefaultPassword()));
            admin.setPassword(adminProperties.getAuth().getDefaultPassword());
            admin.setNickname("超级管理员");
            admin.setEmail("admin@admin.com");
            admin.setCreateTime(LocalDateTime.now());
            admin.setUpdateTime(LocalDateTime.now());
            admin.setStatus(1);
            admin.setRole(1); // 明确设置角色为1（超级管理员）

            adminService.saveAdmin(admin);
            System.out.println("初始化管理员账号: " +
                    adminProperties.getAuth().getDefaultUsername() + "/" +
                    adminProperties.getAuth().getDefaultPassword());
        }
    }
}