package com.example.campus_blog_forum_system.mapper;

import com.example.campus_blog_forum_system.pojo.AdminUser;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AdminMapper
{
    @Select("SELECT " +
            "id, username, password, nickname, " +
            "email, create_time AS createTime, " +
            "update_time AS updateTime, status, role, " + // 添加role
            "login_attempts, last_failed_login_time " +  // 添加这两个字段
            "FROM admin_user " +
            "WHERE username = #{username}")
    AdminUser findByUsername(String username);

    @Select("SELECT " +
            "id, username, password, nickname, " +
            "email, create_time AS createTime, " +
            "update_time AS updateTime, status, role " + // 添加role
            "FROM admin_user")
    List<AdminUser> findAll();

    @Insert("INSERT INTO admin_user(" +
            "username, password, nickname, email, " +
            "create_time, update_time, status, role) " +
            "VALUES(" +
            "#{username}, #{password}, #{nickname}, #{email}, " +
            "#{createTime}, #{updateTime}, #{status}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(AdminUser adminUser);

    @Update("UPDATE admin_user SET " +
            "username = #{username}, " +
            "nickname = #{nickname}, " +
            "email = #{email}, " +
            "update_time = #{updateTime}, " +
            "status = #{status}, " +
            "role = #{role}," + // 添加role更新
            "login_attempts = #{loginAttempts}, " +  // 添加这行
            "last_failed_login_time = #{lastFailedLoginTime} " +  // 添加这行
            "WHERE id = #{id}")
    void update(AdminUser adminUser);

    @Delete("DELETE FROM admin_user WHERE id=#{id}")
    void delete(Long id);

    @Update("UPDATE admin_user SET status = #{status}, update_time = #{updateTime} WHERE id = #{id}")
    void updateStatus(AdminUser admin);

    @Select("SELECT role FROM admin_user WHERE username = #{username}")
    Integer findRoleByUsername(String username);

    @Select("SELECT * FROM admin_user WHERE id = #{id}")
    AdminUser findById(Long id);

    @Update("UPDATE admin_user SET password = #{password}, update_time = #{updateTime} WHERE id = #{id}")
    void updatePassword(AdminUser adminUser);

}
