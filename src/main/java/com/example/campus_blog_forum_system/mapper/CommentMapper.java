package com.example.campus_blog_forum_system.mapper;

import com.example.campus_blog_forum_system.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO `comment`(article_id, user_id, content, parent_id, created_time, update_time) " +
            "VALUES(#{articleId}, #{userId}, #{content}, #{parentId}, #{createdTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "commentId")
    int insert(Comment comment);

    @Select("SELECT * FROM `comment` WHERE comment_id = #{commentId}")
    Comment selectById(Integer commentId);

    @Select("SELECT * FROM `comment` WHERE article_id = #{articleId} ORDER BY created_time DESC")
    List<Comment> selectByArticleId(Integer articleId);

    @Select("SELECT * FROM `comment` WHERE parent_id = #{parentId} ORDER BY created_time ASC")
    List<Comment> selectReplies(Integer parentId);

    @Update("UPDATE `comment` SET content = #{content}, update_time = #{updateTime} WHERE comment_id = #{commentId}")
    int update(Comment comment);

    @Delete("DELETE FROM `comment` WHERE comment_id = #{commentId}")
    int deleteById(Integer commentId);

    @Delete("DELETE FROM `comment` WHERE article_id = #{articleId}")
    int deleteByArticleId(Integer articleId);

    @Select("SELECT * FROM `comment` WHERE user_id = #{userId}")
    List<Comment> selectByUserId(Integer userId);
}
