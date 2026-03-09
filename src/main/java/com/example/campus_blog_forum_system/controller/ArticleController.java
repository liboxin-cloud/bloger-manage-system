package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Article;
import com.example.campus_blog_forum_system.pojo.PageBean;
import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.service.ArticleService;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import com.github.pagehelper.PageInfo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

//    @PostMapping
//    public Result<Void> add(@RequestBody @Valid Article article)
//    {
//        articleService.add(article);
//        return Result.success();
//
//    }


    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Valid Article article)
    {
        try {
            // 获取当前登录用户信息
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = Integer.parseInt((String) claims.get("id"));
            String username = (String) claims.get("username");

            // 设置文章属性
            article.setCreateUser(userId);


            Map<String, Object> map = ThreadLocalUtil.get();
            Object roleObj = map.get("role");
            int role = 0;
            if (roleObj != null) {
                try {
                    role = Integer.parseInt(roleObj.toString());
                } catch (NumberFormatException e) {
                }
            }


            //创建者身份0 -- 普通用户， 1 -- 管理员
            article.setCreatorType(role); // 设置创建者类型
            article.setCreateTime(LocalDateTime.now());
            article.setUpdateTime(LocalDateTime.now());



            // 如果状态为空，默认为草稿
            if (article.getState() == null || article.getState().isEmpty()) {
                article.setState("草稿");
            }


            articleService.add(article);
            return Result.success("文章发布成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文章发布失败: " + e.getMessage());
        }
    }

    // 获取文章列表（分页）
    @GetMapping("/page")
    public Result<PageBean<Article>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state) {
        try {
            PageBean<Article> pageBean = articleService.list(pageNum, pageSize, categoryId, state);
            return Result.successWithData(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();

        }
    }


    @GetMapping("/{id}")
    public Result<Article> getById(@PathVariable Integer id) {
        try {
            Article article = articleService.findById(id);
            if (article == null) {
                return new Result<>();
            }
            return Result.successWithData(article);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

    // 更新文章
    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid Article article) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = Integer.parseInt((String) claims.get("id"));

            // 检查文章是否存在且属于当前用户
            Article existingArticle = (Article) articleService.findByCreateUser(article.getId());
            if (existingArticle == null) {
                return Result.error("文章不存在");
            }
            if (!existingArticle.getCreateUser().equals(userId)) {
                return Result.error("无权修改此文章");
            }

            article.setUpdateTime(LocalDateTime.now());
            articleService.update(article);
            return Result.success("文章更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文章更新失败");
        }
    }

    // 删除文章
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = Integer.parseInt((String) claims.get("id"));

            Article article = (Article) articleService.findByCreateUser(id);
            if (article == null) {
                return Result.error("文章不存在");
            }
            if (!article.getCreateUser().equals(userId)) {
                return Result.error("无权删除此文章");
            }

            articleService.deleteById(id);
            return Result.success("文章删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文章删除失败");
        }
    }

    // 获取我的文章列表
    @GetMapping("/my")
    public Result<List<Article>> getMyArticles() {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            Integer userId = Integer.parseInt((String) claims.get("id"));

            List<Article> articles = articleService.findByCreateUser(userId);
            return Result.successWithData(articles);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();

        }
    }

    // 获取热门文章列表（热度 > 5）
    @GetMapping("/hot")
    public Result<PageBean<Article>> getHotArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId) {
        try {
            PageBean<Article> pageBean = articleService.findHotArticles(pageNum, pageSize, categoryId);
            return Result.successWithData(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

}
