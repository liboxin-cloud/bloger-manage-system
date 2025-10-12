package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.mapper.UserMapper;
import com.example.campus_blog_forum_system.pojo.User;
import com.example.campus_blog_forum_system.service.UserService;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService
{
    @Autowired
    private UserMapper userMapper;

    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findUserByName(String username)
    {
        return userMapper.findUserByName(username);
    }

    @Override
    public String getPassword(String username)
    {
        return userMapper.getPassword(username);
    }

    public void register(String username, String password)
    {
        //判断用户名是不是为空
        if(username == null || username.trim().isEmpty())
        {
            throw new IllegalArgumentException("用户名不能为空");
        }
        //判断密码是不是为空
        if(password == null || password.trim().isEmpty())
        {
            throw new IllegalArgumentException("密码不能为空");
        }
        //进行密码加密
        String hashedPwd = passwordEncoder.encode(password);
        //添加新的用户信息
        userMapper.add(username, hashedPwd);
    }

    @Override
    public void update(User user)
    {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }


    @Override
    public void updateAvatar(String avatarUrl)
    {
        Map<String, Object> map = ThreadLocalUtil.get();
        String id = (String) map.get("id");
        userMapper.updateAvatar(avatarUrl, id);

     }

    @Override
    public void updatePwd(String newPwd)
    {
        Map<String, Object> map = ThreadLocalUtil.get();
        String id = (String) map.get("id");

        //使用更安全的方式进行密码加密
        String hashedPwd = passwordEncoder.encode(newPwd);
        userMapper.updatePwd(hashedPwd, id);

    }

    @Override
    public PageInfo<User> findAllUsers(int PageNum, int PageSize)
    {
        PageHelper.startPage(PageNum, PageSize);
        List<User> users = userMapper.findAllUsers();
        return new PageInfo<>(users);
    }

    @Override
    public PageInfo<User> findUsersByKeyword(int page, int size, String keyword)
    {
        //开启分页功能
        PageHelper.startPage(page, size);

        List<User> users;
        if(keyword != null && !keyword.isEmpty())
        {
            String searchPattern = "%" + keyword.trim() + "%";
            System.out.println("搜索关键词：" + searchPattern); // 调试用
            users = userMapper.selectByUsernameLike(searchPattern);

        }
        else
        {
            //如果关键词是空的，则查询全部用户
            users = userMapper.selectAll();

        }
        return new PageInfo<>(users);

    }

    @Override
    public void updateUserStatus(Long userId, Integer status)
    {
        //校验状态值是否合法
        if(status != 0 && status != 1)
        {
            throw new IllegalArgumentException("无效的状态值，必须为0（封禁）或1（正常）");

        }

        //更新用户状态
        userMapper.updateStatus(userId, status);

    }

}
