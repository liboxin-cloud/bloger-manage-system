package com.example.campus_blog_forum_system.service;

import com.example.campus_blog_forum_system.pojo.AdminUser;
import com.github.pagehelper.PageInfo;

public interface AdminService
{
    AdminUser findByUsername(String username);

    PageInfo<AdminUser> findAllAdmins(int pageNum, int pageSize);
    void saveAdmin(AdminUser adminUser);

    void updateAdmin(AdminUser adminUser);

    void deleteAdmin(Long id);

    //判断当前登录的管理员是不是超级管理员
    boolean isSuperAdmin(AdminUser currentAdmin);

    AdminUser findById(Long id);

    void updatePassword(AdminUser adminUser);

    void updateAdminStatus(Long adminId, Integer status);
}
