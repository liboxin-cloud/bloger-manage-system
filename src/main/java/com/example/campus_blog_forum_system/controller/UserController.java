package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.*;
import com.example.campus_blog_forum_system.service.ArticleService;
import com.example.campus_blog_forum_system.service.CategoryService;
import com.example.campus_blog_forum_system.service.UserService;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.campus_blog_forum_system.utils.JwtUtil;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Validated
@RestController
@RequestMapping("/user")
public class UserController
{
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;
    @PostMapping(value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<Void> register(
            @RequestParam @Pattern(regexp = "^\\S{5,16}$")String username,
            @RequestParam @Pattern(regexp = "^\\S{5,16}$")String password)
    {  // JSON格式参数

        // 参数验证
        if(username == null || username.trim().isEmpty())
        {
            Result.error("用户名不能为空");

        }
        if(password == null || password.trim().isEmpty())
        {
            Result.error("密码不能为空");
        }
        if(userService.findUserByName(username) != null)
        {
            return Result.error("用户名已存在");

        }
        // 注册用户
        userService.register(username, password);
        return Result.success();
    }
    @RequestMapping("/login")
    public Result<Map<String, Object>> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password) {
        // 根据用户名来进行用户查询
        User loginUser = userService.findUserByName(username);
        // 判断用户是否存在
        if(loginUser == null) {
            return new Result<>(1, "用户名不存在", null);
        }

        if(loginUser.getStatus() != 1) {
            return new Result<>(1, "用户被禁用", null);
        }

        // 判断密码是否正确
        try {
            if (!passwordEncoder.matches(password, loginUser.getPassword())) {
                return new Result<>(1, "用户名或密码错误,登录失败", null);
            }

            // 生成token和用户信息
            Map<String, String> claims = new HashMap<>();
            claims.put("id", loginUser.getId().toString());
            claims.put("username", username);
            claims.put("role", "USER"); // 添加角色信息

            String token = JwtUtil.genToken(claims);

            // 登录成功返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("username", username);
            return Result.successWithData(data);

        } catch(Exception e) {
            return new Result<>(1, "密码错误: " + e.getMessage(), null);
        }
    }


    @GetMapping("/userInfo")
    public Result<User> userInfo(@RequestHeader(name = "Authorization") String token)
    {
        String tokenValue = token;

        if (token != null && token.startsWith("Bearer ")) {
            //去掉Bearer前缀
            tokenValue = token.substring(7);
        }
        //根据用户名相关信息来查询用户
        Map<String, Object> map = JwtUtil.parseToken(tokenValue);
        String username = (String) map.get("username");

        User user = userService.findUserByName(username);
        //return Result.success(String.valueOf(user));

        return Result.successWithData(user);
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Validated User user)
    {
        userService.update(user);
        return Result.success();
    }

    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl)
    {
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params)
    {
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");
        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd))
        {
            return Result.error("缺少必要的参数");

        }

        //检查新密码和原始密码是否一样
        if(!rePwd.equals(newPwd))
        {
            return Result.error("两次填写的新密码不一致");

        }

        //检查原密码是否正确
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findUserByName(username);
        String hashedOldPwd = DigestUtils.md5Hex(oldPwd);
        if(!passwordEncoder.matches(oldPwd, loginUser.getPassword()))
        {
            return Result.error("原始密码填写不正确");
        }



        //调用service接口进行密码更新
        userService.updatePwd(newPwd);
        return Result.success();

    }

    @PostMapping("/article")
    public Result<Void> addArticle(@RequestBody Article article) {
        try {
            // 获取当前用户信息
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return Result.error("未登录");
            }

            // 验证必要的字段
            if (article.getTitle() == null || article.getTitle().trim().isEmpty()) {
                return Result.error("标题不能为空");
            }
            if (article.getContent() == null || article.getContent().trim().isEmpty()) {
                return Result.error("内容不能为空");
            }
            if (article.getCoverImg() == null || article.getCoverImg().trim().isEmpty()) {
                return Result.error("封面不能为空");
            }
            if (article.getCategoryId() == null) {
                return Result.error("分类不能为空");
            }

            // 设置文章属性
            article.setCreateUser(currentUser.getId());
            article.setCreateTime(LocalDateTime.now());
            article.setUpdateTime(LocalDateTime.now());
            // 设置创建者类型：0-普通用户（根据你的常量定义）
            article.setCreatorType(0); // 假设0是普通用户，1是管理员

            // 如果状态为空，默认为草稿
            if (article.getState() == null || article.getState().isEmpty()) {
                article.setState("草稿");
            }

            // 保存文章
            articleService.add(article);
            return Result.success("文章发布成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败: " + e.getMessage());
        }
    }

    @GetMapping("/my/articles")
    public Result<PageBean<Article>> getMyArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state) {

        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return new Result<>();
            }

            if (pageNum == null || pageNum < 1) pageNum = 1;
            if (pageSize == null || pageSize < 1) pageSize = 10;

            PageBean<Article> pb = articleService.listByUserId(
                    currentUser.getId(), pageNum, pageSize, categoryId, state);
            return Result.successWithData(pb);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }
    @GetMapping("/article")
    public Result<PageBean<Article>> list(
            Integer pageNum,
            Integer pageSize,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String state
    )
    {

        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        PageBean<Article> pb = articleService.list(pageNum, pageSize, categoryId, state);
        return Result.successWithData(pb);
    }

    @GetMapping("/category")
    public Result<List<Category>> listCategory()
    {
        try
        {
            List<Category> categories = categoryService.listAll();
            return Result.successWithData(categories);

        }catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }

    }

    private User getCurrentUser() {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims == null) return null;

            String username = (String) claims.get("username");
            if (username == null) return null;


            return userService.findUserByName(username);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable Integer id) {
        try {
            User currentUser = getCurrentUser();
            if (currentUser == null) {
                return Result.error("未登录");
            }

            if (id == null) {
                return Result.error("文章ID不能为空");
            }
            // 验证文章是否属于当前用户
            Article existingArticle = articleService.findById(id);
            if (existingArticle == null) {
                return Result.error("文章不存在");
            }
            if (!existingArticle.getCreateUser().equals(currentUser.getId())) {
                return Result.error("无权删除此文章");
            }

            articleService.deleteById(id);
            return Result.success("文章删除成功");

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
