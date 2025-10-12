package com.example.campus_blog_forum_system.service;

import com.example.campus_blog_forum_system.pojo.Comment;

import java.util.List;

public interface CommentService {

    Comment getCommentById(Integer commentId);

    boolean addComment(Comment comment);

    List<Comment> getCommentsByArticleId(Integer commentId);

    List<Comment> getReplyComments(Integer parentId);

    List<Comment> getCommentsByUserId(Integer userId);

    boolean updateComment(Comment comment);

    boolean deleteComment(Integer commentId);

    boolean deleteCommentsByArticleId(Integer articleId);
}
