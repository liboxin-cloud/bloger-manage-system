package com.example.campus_blog_forum_system.service;

import com.example.campus_blog_forum_system.pojo.User;
import com.github.pagehelper.PageInfo;

public interface UserService
{
    public User findUserByName(String username);

    public String getPassword(String username);
    void register(String username, String password, String email);

    void update(User user);

    void updateAvatar(String avatarUrl);

    void updatePwd(String newPwd);

    PageInfo<User> findAllUsers(int pageNum, int pageSize);

    void updateUserStatus(Long userId, Integer status);

    PageInfo<User> findUsersByKeyword(int page, int size, String keyword);

    public User findUserByEmail(String email);
}
