package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.constant.AdminRoleConstant;
import com.example.campus_blog_forum_system.mapper.AdminMapper;
import com.example.campus_blog_forum_system.pojo.AdminUser;
import com.example.campus_blog_forum_system.service.AdminService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AdminServiceImpl implements AdminService
{
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public AdminUser findByUsername(String username)
    {
        return adminMapper.findByUsername(username);
    }

    @Override
    public PageInfo<AdminUser> findAllAdmins(int pageNum, int pageSize)
    {
        PageHelper.startPage(pageNum, pageSize);
        List<AdminUser> admins = adminMapper.findAll();
        return new PageInfo<>(admins);
    }

    @Override
    public void saveAdmin(AdminUser adminUser)
    {


        adminUser.setId(adminUser.getId());
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setCreateTime(LocalDateTime.now());
        adminUser.setUpdateTime(LocalDateTime.now());
        adminUser.setStatus(1);
        //adminUser.setRole(0);
        if(adminUser.getRole() == null)
        {
            adminUser.setRole(0);
        }

        adminMapper.insert(adminUser);

    }

    @Override
    public void updateAdmin(AdminUser adminUser)
    {
        adminUser.setUpdateTime(LocalDateTime.now());
        adminMapper.update(adminUser);
    }
    @Override
    public void deleteAdmin(Long id)
    {
        try
        {

            System.out.println("正在删除管理员，ID: " + id);

            // 检查该管理员是否有关联的文章
            // 这里需要注入 ArticleService 或 ArticleMapper 来检查
            // 如果有业务需要，可以先删除或转移该管理员的文章

            adminMapper.delete(id);
            System.out.println("管理员删除成功，ID: " + id);
        }
        catch (Exception e)
        {
            System.err.println("删除管理员时发生错误，ID: " + id + "，错误信息: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("删除管理员失败: " + e.getMessage(), e);
        }

    }

    @Override
    public boolean isSuperAdmin(AdminUser currentAdmin) {
        System.out.println("当前用户角色验证:");
        System.out.println("用户信息: " + currentAdmin);

        if (currentAdmin == null) {
            return false;
        }

        // 直接使用当前用户的角色，不需要再次查询数据库
        Integer role = currentAdmin.getRole();
        System.out.println("用户名: " + currentAdmin.getUsername());
        System.out.println("角色ID: " + role);
        System.out.println("常量值: " + AdminRoleConstant.SUPER_ADMIN);

        return role != null && role == AdminRoleConstant.SUPER_ADMIN;
    }

    @Override
    public void updateAdminStatus(Long adminId, Integer status)
    {



        if(status != 0 && status != 1)
        {
            throw new IllegalArgumentException("无效的状态值，状态值只能是0（封禁）或1（正常）");

        }
        System.out.println("当前用户角色验证:");
        System.out.println("用户id: " + adminId + "用户状态：" + status);
        AdminUser admin = new AdminUser();
        admin.setId(adminId);
        admin.setStatus(status);
        admin.setUpdateTime(LocalDateTime.now());
        adminMapper.updateStatus(admin);
    }

    @Override
    public AdminUser findById(Long id)
    {
        return adminMapper.findById(id);
    }

    @Override
    public void updatePassword(AdminUser adminUser)
    {
        adminUser.setUpdateTime(LocalDateTime.now());
        adminMapper.updatePassword(adminUser);
    }
}
