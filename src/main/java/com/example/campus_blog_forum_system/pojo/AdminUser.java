package com.example.campus_blog_forum_system.pojo;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.message.AsynchronouslyFormattable;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@Table(name = "admin_user")
@Getter
@Setter

public class AdminUser
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;

    @NotNull
    @NotBlank(message = "用户名不能为空")
    private String username;
    private String password;
    private String nickname;
    private String avatar;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status; // 0-禁用 1-正常 2-锁定
    @Column(name = "role", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer role;
    private Integer loginAttempts = 0;
    private Date lastFailedLoginTime;
}
