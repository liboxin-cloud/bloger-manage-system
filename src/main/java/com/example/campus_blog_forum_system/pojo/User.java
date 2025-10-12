package com.example.campus_blog_forum_system.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User
{
    @Getter
    @NotNull
    private Integer id;//主键ID
    private String username;//用户名
    @JsonIgnore
    private String password;//密码

    @NotEmpty
    @Pattern(regexp = "^\\\\S{1,10}$")
    private String nickname;//昵称

    @NotEmpty
    @Email
    private String email;//邮箱
    private String userPic;//用户头像地址
    private LocalDateTime createTime;//创建时间

    @Setter
    @Getter
    private Integer status;//用户状态 0 -> 封禁 1-> 正常


    //更新时间
    @Setter
    private LocalDateTime updateTime;


}
