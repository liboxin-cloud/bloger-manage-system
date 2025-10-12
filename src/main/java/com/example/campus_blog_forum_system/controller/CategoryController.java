package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Category;
import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController
{
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Result add(@RequestBody @Validated Category category)
    {
        categoryService.add(category);
        return Result.success();
    }

    @GetMapping
    public Result<List<Category>> list()
    {
        List<Category> cs = categoryService.listCategoriesByUser();
        return Result.successWithData(cs);
    }
    @GetMapping("/detail")
    public Result<Category> detail(Integer id)
    {
        Category c = categoryService.findUserById(id);
        return Result.successWithData(c);
    }

    @PutMapping
    public Result<Void> update(@RequestBody @Validated Category category)
    {
        categoryService.update(category);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> delete(Integer id)
    {
        categoryService.delete(id);
        return Result.success();
    }

}
