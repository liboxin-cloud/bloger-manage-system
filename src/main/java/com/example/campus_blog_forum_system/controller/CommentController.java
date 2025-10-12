package com.example.campus_blog_forum_system.controller;


import com.example.campus_blog_forum_system.pojo.Comment;
import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.pojo.User;
import com.example.campus_blog_forum_system.service.CommentService;
import com.example.campus_blog_forum_system.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpRequest;
import java.util.List;

@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;



    //添加评论
    @PostMapping("/add")
    public Result<Void> addComment(@RequestBody Comment comment, Authentication authentication) {
        //检查用户是否登录
        if (authentication == null || !authentication.isAuthenticated()) {
            return Result.error("未登录");
        }

        // 从 JWT 认证信息中获取用户ID
        String username = authentication.getPrincipal().toString();
        User user = userService.findUserByName(username);
        Integer userId = user.getId();

        comment.setUserId(userId);

        boolean result = commentService.addComment(comment);
        if (result) {
            return Result.success("评论发表成功");
        } else {
            return Result.error("评论发表失败");
        }
    }

    @GetMapping("/list/{articleId}")
    public Result<List<Comment>> getComments(@PathVariable Integer articleId) {
        List<Comment> comments = commentService.getCommentsByArticleId(articleId);
        return Result.successWithData(comments);
    }

    //删除评论
    @DeleteMapping("/delete/{commentId}")
    public Result<Void> deleteComment(@PathVariable Integer commentId, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if(userId == null) {
            return Result.error("请先登录");
        }
        Comment comment = commentService.getCommentById(commentId);
        if(comment == null) {
            return Result.error("评论不存在");
        }

        if(!comment.getUserId().equals(userId)) {
            return Result.error("无权限删除");
        }

        boolean result = commentService.deleteComment(commentId);


        if (result) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }

}
