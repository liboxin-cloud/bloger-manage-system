package com.example.campus_blog_forum_system.service.impl;

import com.example.campus_blog_forum_system.mapper.CategoryMapper;
import com.example.campus_blog_forum_system.pojo.Category;
import com.example.campus_blog_forum_system.service.CategoryService;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl implements CategoryService
{
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void add(Category category)
    {

        if(category.getCategoryName() == null || category.getCategoryName().isEmpty())
        {
            throw new IllegalArgumentException("分类名称不能为空");

        }

        if (category.getCategoryAlias() == null || category.getCategoryAlias().isEmpty())
        {
            throw new IllegalArgumentException("分类别名不能为空");
        }
        if(category.getCreateUser() == null)
        {
            throw new IllegalArgumentException("创建用户不能为空");
        }
        categoryMapper.add(category);


//        if(category == null)
//        {
//            throw new IllegalArgumentException("分类信息不能为空");
//        }
//        if(StringUtils.isNullOrEmpty(category.getCategoryName()))
//        {
//            throw new IllegalArgumentException("分类名称不能为空");
//        }
//
//
//        Map<String, Object> map = ThreadLocalUtil.get();
//        Integer userId = null;
//        try
//        {
//            userId = Integer.parseInt(map.get("id").toString());
//        }
//        catch (Exception e)
//        {
//            throw new IllegalArgumentException("无效的用户ID格式");
//        }
//        //补充属性值
//        category.setCreateTime(LocalDateTime.now());
//        category.setUpdateTime(LocalDateTime.now());
//        category.setCreateUser(userId);
//        try
//        {
//            categoryMapper.add(category);
//        }
//        catch(Exception e)
//        {
//            throw new RuntimeException("创建分类失败: " + e.getMessage(), e);
//        }


    }


    @Override
    public List<Category> listCategoriesByUser()
    {
        Map<String,Object> map = ThreadLocalUtil.get();
        String userId = (String) map.get("id");
        return categoryMapper.list(Integer.valueOf(userId));
    }

    @Override
    public Category findUserById(Integer id)
    {
        return categoryMapper.findUserById(id);
    }

    @Override
    public void delete(Integer id)
    {
        categoryMapper.delete(id);
    }

    @Override
    public void update(Category category)
    {
        category.setUpdateTime(LocalDateTime.now());
        categoryMapper.update(category);
    }

    @Override
    public List<Category> listAll()
    {
        return categoryMapper.selectAll();

    }

    @Override
    public void addCategory(Category category)
    {
        categoryMapper.insert(category);

    }

    @Override
    public void deleteById(Integer id)
    {
        categoryMapper.deleteById(id);

    }




}
