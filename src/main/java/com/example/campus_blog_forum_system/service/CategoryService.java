package com.example.campus_blog_forum_system.service;

import com.example.campus_blog_forum_system.pojo.Category;

import java.util.List;

public interface CategoryService
{
    //添加文章
    void add(Category category);

    //查询文章分类列表
    List<Category> listCategoriesByUser();

    Category findUserById(Integer id);

    void update(Category category);

    void delete(Integer id);

    List<Category> listAll();

    void addCategory(Category category);

    void deleteById(Integer id);


}
