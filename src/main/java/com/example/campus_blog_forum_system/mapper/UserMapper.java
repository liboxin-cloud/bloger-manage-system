package com.example.campus_blog_forum_system.mapper;

import com.example.campus_blog_forum_system.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper
{
    @Select("SELECT * FROM user WHERE username = #{username}")
    public User findUserByName(String username);
    @Select("SELECT password FROM user WHERE username = #{username}")
    public String getPassword(String username);

    //添加用户
    @Insert("insert into user(username,password,create_time,update_time)"+
            "values(#{username},#{password},now(),now())")
    void add(String username, String password);

    @Update("update user set nickname=#{nickname},email=#{email},update_time=#{updateTime} where id=#{id}")
    void update(User user);

    @Update("update user set user_pic=#{avatarUrl},update_time=now() where id=#{id}")
    void updateAvatar(String avatarUrl, String id);

    @Update("update user set password=#{hashedPassword},update_time=now() where id=#{id}")
    void updatePwd(String hashedPassword, String id);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> findAllUsers();

    @Update("UPDATE user SET status=#{status}, update_time=NOW() WHERE id=#{userId}")
    void updateStatus(@Param("userId") Long userId, @Param("status") Integer status);

    @Select("SELECT * FROM user WHERE username LIKE #{keyword} ORDER BY create_time DESC")
    List<User> selectByUsernameLike(@Param("keyword")String keyword);

    @Select("SELECT * FROM user ORDER BY create_time DESC")
    List<User> selectAll();
}
