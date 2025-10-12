package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Article;
import com.example.campus_blog_forum_system.pojo.PageBean;
import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.service.ArticleService;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController
{
    @GetMapping("/list")
    public Result<String> list()
    {
        // 明确指定返回字符串数据
        return Result.successWithData("所有文章类型");
    }
    @Autowired
    private ArticleService articleService;

    @PostMapping
    public Result<Void> add(@RequestBody @Valid Article article)
    {
        articleService.add(article);
        return Result.success();

    }



}
