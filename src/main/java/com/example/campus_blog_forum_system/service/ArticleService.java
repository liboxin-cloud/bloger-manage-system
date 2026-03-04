package com.example.campus_blog_forum_system.service;

import com.example.campus_blog_forum_system.pojo.Article;
import com.example.campus_blog_forum_system.pojo.PageBean;

import java.util.List;

public interface ArticleService
{
    //添加文章
    void add(Article article);


    //删除文章
    public void deleteById(Integer id);

    //查找是否有分类id
    public boolean update(Article article);

    List<Article> findByCategoryId(Integer categoryId);


    PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state);

    PageBean<Article> listByUserId(Integer userId, Integer pageNum, Integer pageSize, Integer categoryId, String state);

    List<Article> findByCreateUser(Integer userId);

    public Article findById(Integer id);

    PageBean<Article> findHotArticles(Integer pageNum, Integer pageSize, Integer categoryId);

    PageBean<Article> findViolationArticles(Integer pageNum, Integer pageSize, Integer categoryId);
}
