package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.mapper.CommentMapper;
import com.example.campus_blog_forum_system.pojo.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.campus_blog_forum_system.service.CommentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    CommentMapper commentMapper;

    @Override
    public Comment getCommentById(Integer id) {
        return commentMapper.selectById(id);
    }

    @Override
    public boolean addComment(Comment comment) {
        return commentMapper.insert(comment) > 0;
    }

    @Override
    public boolean updateComment(Comment comment) {
        return commentMapper.update(comment) > 0;
    }

    @Override
    public List<Comment> getCommentsByArticleId(Integer articleId) {
        return commentMapper.selectByArticleId(articleId);
    }

    @Override
    public List<Comment> getCommentsByUserId(Integer userId) {
        return commentMapper.selectByUserId(userId);
    }

    @Override
    public List<Comment> getReplyComments(Integer parentId) {
        return commentMapper.selectReplies(parentId);
    }

    @Override
    public boolean deleteComment(Integer commentId) {
        return commentMapper.deleteById(commentId) > 0;
    }

    @Override
    public boolean deleteCommentsByArticleId(Integer articleId) {
        return commentMapper.deleteByArticleId(articleId) > 0;
    }

}
