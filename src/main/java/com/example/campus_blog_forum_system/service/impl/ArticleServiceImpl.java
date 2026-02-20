package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.mapper.ArticleMapper;
import com.example.campus_blog_forum_system.pojo.Article;
import com.example.campus_blog_forum_system.pojo.PageBean;
import com.example.campus_blog_forum_system.service.ArticleService;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService
{
    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public void add(Article article)
    {
//// 补充属性值
//        article.setCreateTime(LocalDateTime.now());
//        article.setUpdateTime(LocalDateTime.now());
//
//        Map<String, Object> map = ThreadLocalUtil.get();
//        String userId = (String) map.get("id");
//        article.setCreateUser(Integer.valueOf(userId));
//
//        articleMapper.add(article);

        // 确保 categoryId 不为 null
        if (article.getCategoryId() == null) {
            throw new IllegalArgumentException("分类ID不能为空");
        }

        articleMapper.add(article);

    }

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        // 参数验证和默认值设置
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        if (pageSize > 100) { // 限制最大页面大小
            pageSize = 100;
        }

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 获取用户信息
        Map<String, Object> map = ThreadLocalUtil.get();
        System.out.println("ThreadLocal中的数据: " + map);

        Object userIdObj = map.get("id");
        String userId = null;
        if (userIdObj != null) {
            userId = userIdObj.toString();
        }

        // 检查是否是管理员
        Object roleObj = map.get("role");
        boolean isAdmin = false;
        if (roleObj != null) {
            try {
                int role = Integer.parseInt(roleObj.toString());
                isAdmin = (role == 1 || role == 0); // 假设1是超级管理员
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        System.out.println("当前用户ID: " + userId);
        System.out.println("是否是管理员: " + isAdmin);
//
        List<Article> articles;
        if (isAdmin) {

            // 管理员可以查看所有文章
            articles = articleMapper.listAll(categoryId, state);
        } else if (userId != null && !userId.isEmpty()) {
            // 普通用户只查看自己的文章
            articles = articleMapper.listAll(categoryId, state);
        } else {
            // 没有用户信息，返回空列表
            System.out.println("警告：用户ID为空，且不是管理员，无法查询文章");
            articles = new ArrayList<>();
        }

        System.out.println("查询到的文章数量: " + articles.size());

        // 使用PageInfo包装结果
        PageInfo<Article> pageInfo = new PageInfo<>(articles);

        // 创建并填充PageBean对象
        PageBean<Article> pb = new PageBean<>();
        pb.setTotal(pageInfo.getTotal());
        pb.setPageNum(pageInfo.getPageNum());
        pb.setPageSize(pageInfo.getPageSize());
        pb.setPages(pageInfo.getPages());
        pb.setItems(pageInfo.getList());
        pb.setHasNextPage(pageInfo.isHasNextPage());
        pb.setHasPreviousPage(pageInfo.isHasPreviousPage());

        return pb;
    }

    @Override
    public void deleteById(Integer id)
    {
        articleMapper.deleteById(id);
    }

    @Override
    public List<Article> findByCategoryId(Integer categoryId)
    {
        return articleMapper.findCategoryId(categoryId);

    }

    @Override
    public List<Article> findByCreateUser(Integer userId)
    {
        return articleMapper.findByCreateUser(userId);
    }

    @Override
    public boolean update(Article article) {
        if (article != null) {
            article.setUpdateTime(LocalDateTime.now());
            articleMapper.update(article);
            return true;
        }

        return false;
    }

    @Override
    public PageBean<Article> listByUserId(Integer userId, Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        // 参数验证
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        if (pageSize > 100) pageSize = 100;

        // 开启分页
        PageHelper.startPage(pageNum, pageSize);

        // 查询当前用户的文章
        List<Article> articles = articleMapper.listByUserId(userId, categoryId, state);

        // 使用PageInfo包装结果
        PageInfo<Article> pageInfo = new PageInfo<>(articles);

        // 创建并填充PageBean对象
        PageBean<Article> pb = new PageBean<>();
        pb.setTotal(pageInfo.getTotal());
        pb.setPageNum(pageInfo.getPageNum());
        pb.setPageSize(pageInfo.getPageSize());
        pb.setPages(pageInfo.getPages());
        pb.setItems(pageInfo.getList());
        pb.setHasNextPage(pageInfo.isHasNextPage());
        pb.setHasPreviousPage(pageInfo.isHasPreviousPage());

        return pb;
    }

    @Override
    public Article findById(Integer id) {
        Article article = articleMapper.findById(id);
        return article;
    }
}




