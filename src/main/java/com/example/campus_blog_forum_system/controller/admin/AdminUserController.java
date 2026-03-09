package com.example.campus_blog_forum_system.controller.admin;

import com.example.campus_blog_forum_system.constant.AdminRoleConstant;
import com.example.campus_blog_forum_system.mapper.AdminMapper;
import com.example.campus_blog_forum_system.pojo.*;
import com.example.campus_blog_forum_system.service.ArticleService;
import com.example.campus_blog_forum_system.service.CategoryService;
import com.example.campus_blog_forum_system.service.UserService;
import com.example.campus_blog_forum_system.service.impl.CategoryServiceImpl;
import com.example.campus_blog_forum_system.service.impl.UserServiceImpl;
import com.example.campus_blog_forum_system.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;
import com.example.campus_blog_forum_system.service.AdminService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.http.HttpRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminUserController
{
    @Autowired
    private CategoryServiceImpl categoryServiceImpl;

    public AdminUserController() {
        System.out.println("AdminUserController 被创建");
    }

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private View error;

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @GetMapping("/dashboard")
    public String dashBoard(Model model)
    {
        model.addAttribute("adminCount", adminService.findAllAdmins(1, 10).getTotal());
        return "admin/dashboard";
    }

    @GetMapping("/admins")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')") // 添加注解
    public Result<PageInfo<AdminUser>> listAdmins(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size,
                                                  Model model) {
        // 移除手动权限检查，依赖注解
        AdminUser currentAdmin = getCurrentAdmin();
        if(currentAdmin == null) {
            System.out.println("currentAdmin is null");
            return new Result<>();
        }

        System.out.println("当前管理员: " + currentAdmin.getUsername() + ", 角色: " + currentAdmin.getRole());


        if(currentAdmin.getRole() == AdminRoleConstant.NORMAL_ADMIN)
        {
            return new Result<>();
        }
        PageInfo<AdminUser> pageInfo = adminService.findAllAdmins(page, size);
        return Result.successWithData(pageInfo);
    }

    @PostMapping("/admins/add")
    public Map<String, String> addAdmin(@RequestBody AdminUser adminUser) {

        System.out.println("Received adminUser: " + adminUser); // 打印 adminUser 对象

        Map<String, String> result = new HashMap<>();
        AdminUser currentAdmin = getCurrentAdmin();

        if(currentAdmin == null) {
            result.put("status", "fail");
        }

        if (currentAdmin != null) {
            System.out.println("currentAdmin is not null is:" + currentAdmin);
            System.out.println(currentAdmin.getRole());
        }

        if (!adminService.isSuperAdmin(currentAdmin)) {
            result.put("error", "无权限：仅超级管理员可添加管理员");
            return result;
        }
        try {
            adminService.saveAdmin(adminUser);
            result.put("message", "管理员添加成功");
        } catch (Exception e) {
            result.put("error", "添加失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/admins/edit/{id}")
    public String editAdminForm(@PathVariable Long id, Model model)
    {
        AdminUser adminUser = adminService.findAllAdmins(1, 10).getList()
                .stream()
                .filter(a -> a.getId().equals(id)).findFirst().orElse(null);
        model.addAttribute("admin", adminUser);
        return "admin/admins/edit";

    }

    @PostMapping("/admins/edit/{id}")
    public String updateAdmin(@PathVariable Long id, @ModelAttribute AdminUser adminUser)
    {
        adminUser.setId(id);
        adminService.updateAdmin(adminUser);
        return "redirect:/admin/admins";
    }

//    @GetMapping("/admins/delete/{id}")
//    public String deleteAdmin(@PathVariable Long id)
//    {
//        adminService.deleteAdmin(id);
//        return "redirect:/admin/admins";
//    }


    // 删除管理员
    // 删除管理员
    @DeleteMapping("/admins/delete/{id}")
    public Map<String, String> deleteAdmin(@PathVariable Long id) {
        Map<String, String> result = new HashMap<>();
        try {
            // 检查当前管理员权限
            AdminUser currentAdmin = getCurrentAdmin();
            if (currentAdmin == null) {
                result.put("error", "未登录");
                return result;
            }

            // 只有超级管理员才能删除管理员
            if (!adminService.isSuperAdmin(currentAdmin)) {
                result.put("error", "无权限：仅超级管理员可删除管理员");
                return result;
            }

            // 不能删除自己
            if (currentAdmin.getId().equals(id)) {
                result.put("error", "不能删除自己");
                return result;
            }

            // 检查目标管理员是否存在
            AdminUser targetAdmin = adminService.findById(id);
            if (targetAdmin == null) {
                result.put("error", "管理员不存在");
                return result;
            }

            // 检查是否是超级管理员（根据业务需求决定是否允许删除其他超级管理员）
            if (adminService.isSuperAdmin(targetAdmin)) {
                result.put("error", "不能删除其他超级管理员");
                return result;
            }

            // 检查该管理员是否发表过文章
            List<Article> articles = articleService.findByCreateUser(id.intValue());
            if (articles != null && !articles.isEmpty()) {
                result.put("error", "删除失败：该管理员已发表文章，请先删除其发表的文章");
                return result;
            }

            // 执行删除操作
            adminService.deleteAdmin(id);
            result.put("message", "管理员删除成功");
        } catch (Exception e) {
            System.err.println("删除管理员时发生异常: " + e.getMessage());
            e.printStackTrace();
            result.put("error", "删除失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/users")
    // 移除方法级别的@PreAuthorize注解
    // @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Result<PageInfo<User>> listUsers(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(required = false) String keyword) {
        System.out.println("=== 访问 /admin/users 接口 ===");
        System.out.println("请求参数: page=" + page + ", size=" + size + ", keyword=" + keyword);

        // 添加认证信息日志
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            System.out.println("当前认证用户: " + authentication.getPrincipal());
            System.out.println("用户权限: " + authentication.getAuthorities());
        } else {
            System.out.println("未找到认证信息");
        }

        try {
            PageInfo<User> pageInfo = userService.findUsersByKeyword(page, size, keyword);
            System.out.println("返回用户数据: " + pageInfo);
            return Result.successWithData(pageInfo);
        } catch (Exception e) {
            System.out.println("获取用户列表时发生异常: " + e.getMessage());
            e.printStackTrace();
            return Result.errorWithData(PageInfo.emptyPageInfo());
        }
    }
    //封禁解封用户
    @GetMapping("/users/status/{userId}/{status}")
    public Map<String, String> updateUserStatus(@PathVariable Long userId,
                                                @PathVariable Integer status,
                                                RedirectAttributes redirectAttributes)
    {
        Map<String, String> result = new HashMap<>();
        try
        {
            userService.updateUserStatus(userId, status);
            result.put("message", "用户状态更新成功");
        }
        catch (Exception e)
        {
            result.put("error", "更新失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/admins/{id}")
    public Result<AdminUser> getAdminById(@PathVariable Long id)
    {
        AdminUser currentAdmin = getCurrentAdmin();
        if (!adminService.isSuperAdmin(currentAdmin))
        {
            return new Result<>();
        }

        AdminUser admin = adminService.findById(id);
        if (admin == null)
        {
            return new Result<>();
        }

        return Result.successWithData(admin);
    }


    private AdminUser getCurrentAdmin()
    {
        String currentUserName = getUserNameFromToken();
        System.out.println("currentUserName is :" + currentUserName);
        return adminService.findByUsername(currentUserName);

    }

    private String getUserNameFromToken() {
        //从httpServlet中获取请求头
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String tokenHeader = request.getHeader("Authorization");

        System.out.println(tokenHeader);

        if(tokenHeader != null) {
            // 更健壮的token前缀处理（处理大小写不一致的情况）
            if (tokenHeader.toLowerCase().startsWith("bearer ")) {
                String token = tokenHeader.substring(7); // 去掉"Bearer "前缀（无论大小写）
                System.out.println("token is :" + token);
                return JwtUtil.getUsernameFromToken(token);
            }
            // 使用JwtUtil解析Token中的用户名
            return JwtUtil.getUsernameFromToken(tokenHeader);
        }
        return null;
    }

    // 封禁/启用管理员
    @GetMapping("/admins/status/{adminId}/{status}")
    public Map<String, String> updateAdminStatus(@PathVariable Long adminId,
                                                 @PathVariable Integer status) {
        Map<String, String> result = new HashMap<>();
        try {
            // 检查当前管理员权限
            AdminUser currentAdmin = getCurrentAdmin();
            if (currentAdmin == null) {
                result.put("error", "未登录");
                return result;
            }

            // 只有超级管理员才能修改其他管理员状态
            if (!adminService.isSuperAdmin(currentAdmin)) {
                result.put("error", "无权限：仅超级管理员可修改管理员状态");
                return result;
            }

            // 不能修改自己的状态
            if (currentAdmin.getId().equals(adminId)) {
                result.put("error", "不能修改自己的状态");
                return result;
            }

            // 检查目标管理员是否存在
            AdminUser targetAdmin = adminService.findById(adminId);
            if (targetAdmin == null) {
                result.put("error", "管理员不存在");
                return result;
            }

            // 超级管理员只能由超级管理员修改状态
            if (adminService.isSuperAdmin(targetAdmin)) {
                result.put("error", "不能修改超级管理员状态");
                return result;
            }

            // 更新状态
            targetAdmin.setStatus(status);
            targetAdmin.setUpdateTime(LocalDateTime.now());
            adminService.updateAdmin(targetAdmin);

            result.put("message", "管理员状态更新成功");
        } catch (Exception e) {
            result.put("error", "更新失败: " + e.getMessage());
        }
        return result;
    }

    @GetMapping("/test")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> test() {
        System.out.println("=== 测试端点被访问 ===");
        return ResponseEntity.ok("测试成功");
    }

    @GetMapping("/articles")
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

    //添加文章
    @PostMapping("/articles")
    public Result<Void> addArticle(@RequestBody Article article)
    {
        try
        {
            //获取当前管理员信息
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {
                return Result.error("未登录");

            }

            //设置创建人和创建时间
            article.setCreateUser(currentAdmin.getId().intValue());
            article.setCreateTime(LocalDateTime.now());
            article.setUpdateTime(LocalDateTime.now());
            article.setCreatorType(AdminRoleConstant.ADMIN_CREATOR);


            //验证必要的字段
            if(article.getTitle() == null || article.getTitle().trim().isEmpty())
            {
                return Result.error("标题不能为空");
            }
            if(article.getContent() == null || article.getContent().trim().isEmpty())
            {
                return Result.error("内容不能是空");
            }
            if(article.getCoverImg() == null || article.getCoverImg().trim().isEmpty())
            {
                return Result.error("封面不能为空");
            }


            if(article.getCategoryId() == null)
            {
                return Result.error("分类不能够是空");
            }

            //保存文章
            articleService.add(article);
            return Result.success("文章添加成功");


        }catch(Exception e)
        {
            e.printStackTrace();
            return Result.error("添加失败");

        }
    }

    // 删除文章
    @DeleteMapping("/articles/{id}")
    public Result<Void> deleteArticle(@PathVariable Integer id) {
        try {

            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {
                return Result.error("未登录");

            }

            if(id == null)
            {
                return Result.error("ID是空的");

            }


             articleService.deleteById(id);

            return Result.success("文章删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
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

    @DeleteMapping("/category/{id}")
    public Result<Void> deleteCategory(@PathVariable Integer id)
    {
        try
        {
            //获取当前管理员信息
            AdminUser currentAdmin = getCurrentAdmin();

            if(currentAdmin == null)
            {
                return Result.error("未登录");

            }


            if(id == null)
            {
                return Result.error("ID是空的");
            }



            //检查是否有文章使用该分类
            List<Article> articles = articleService.findByCategoryId(id);
            if(articles != null && !articles.isEmpty())
            {
                return Result.error("删除失败：该分类下还有文章，请先删除相关文章或转移分类");

            }


            //删除分类
            categoryService.deleteById(id);
            return Result.success("分类删除成功");


        }catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败");

        }
    }

    @PostMapping("/category")
    public Result<Void> addCategory(@RequestBody Category category)
    {
        try
        {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {
                return Result.error("未登录");
            }

            if(currentAdmin.getId() == null)
            {
                return Result.error("无效的管理员ID");
            }

            Long adminId = currentAdmin.getId();
            if (adminId > Integer.MAX_VALUE || adminId < Integer.MIN_VALUE)
            {
                return Result.error("用户ID超出范围");
            }


            //设置创建的时间和创建人
            category.setCreateUser(adminId.intValue());
            category.setCreateTime(LocalDateTime.now());
            category.setUpdateTime(LocalDateTime.now());


            //保存分类
            categoryService.add(category);

            return Result.success("分类添加成功");

        }catch(Exception e)
        {
            e.printStackTrace();
            return Result.error("新建分类失败");

        }
    }

    @PutMapping("/category")
    public Result<Void> updateCategory(@RequestBody Category category)
    {
        try
        {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {
                return Result.error("未登录");

            }

            //设置更新时间

            category.setUpdateTime(LocalDateTime.now());

            //更新分类
            categoryService.update(category);
            return Result.success("分类更新成功");

        }catch(Exception e)
        {
            e.printStackTrace();

            return Result.error("更分类失败");

        }
    }

    @GetMapping("/profile")
    public Result<AdminUser> getProfile()
    {
        try
        {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {

                throw new Exception("用户没有登录");

            }
            //不返回密码字段

            currentAdmin.setPassword(null);

            return Result.successWithData(currentAdmin);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return new Result<>();

        }
    }

    //更新管理员信息

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody AdminUser profileData)
    {
        try
        {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null)
            {
                return Result.error("未登录");
            }

            //更新昵称和邮箱
            currentAdmin.setNickname(profileData.getNickname());
            currentAdmin.setEmail(profileData.getEmail());
            currentAdmin.setUpdateTime(LocalDateTime.now());

            //更新数据到数据库

            adminService.updateAdmin(currentAdmin);

            return Result.success("个人信息更新成功");
        }
        catch (Exception e)
        {

            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }




    }


    //修改密码

    //修改密码
    @PutMapping("/profile/password")
    public Result<Void> changePassword(@RequestBody PasswordChangeRequest request) {
        try {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null) {
                return Result.error("未登录");
            }

            //验证原密码
            if(!passwordEncoder.matches(request.getOldPassword(), currentAdmin.getPassword())) {
                return Result.error("原密码错误");
            }

            //验证新密码不能和原密码相同
            if(passwordEncoder.matches(request.getNewPassword(), currentAdmin.getPassword())) {
                return Result.error("新密码不能和原密码相同");
            }

            // 更新密码
            currentAdmin.setPassword(passwordEncoder.encode(request.getNewPassword()));
            currentAdmin.setUpdateTime(LocalDateTime.now());

            // 使用专门的密码更新方法
            adminService.updatePassword(currentAdmin);
            return Result.success("密码修改成功");

        } catch(Exception e) {
            e.printStackTrace();
            return Result.error("密码修改失败: " + e.getMessage());
        }
    }



    @PostMapping("/articles/check-violation/{id}")
    public Result<Map<String, Object>> checkArticleViolation(@PathVariable Integer id) {
        try {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null) {
                return new Result<>();
            }

            Article article = articleService.findById(id);
            if (article == null) {
                return new Result<>();
            }

            // 调用AI检测接口
            // Map<String, Object> aiResult = aiUtil.checkContent(article.getContent());

            // 模拟AI检测结果
            Map<String, Object> result = new HashMap<>();
            boolean isViolation = Math.random() > 0.7; // 30%概率违规
            String reason = isViolation ? "包含敏感词" : "正常";

            // 更新文章违规状态
            article.setIsViolation(isViolation);
            article.setViolationReason(isViolation ? reason : null);
            article.setCheckTime(LocalDateTime.now());
            article.setCheckAdmin(currentAdmin.getId().intValue());

            // 更新到数据库
            // 需要添加 updateViolationStatus 方法到 articleService
            // articleService.updateViolationStatus(article);

            result.put("isViolation", isViolation);
            result.put("reason", reason);

            return Result.successWithData(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

    // 获取违规文章列表
    @GetMapping("/articles/violation")
    public Result<PageBean<Article>> getViolationArticles(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer categoryId) {
        try {
            PageBean<Article> pageBean = articleService.findViolationArticles(pageNum, pageSize, categoryId);
            return Result.successWithData(pageBean);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

    // 手动标记违规
    @PostMapping("/articles/mark-violation/{id}")
    public Result<Void> markViolation(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        try {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null) {
                return Result.error("未登录");
            }

            Article article = articleService.findById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }

            article.setIsViolation(true);
            article.setViolationReason(request.get("reason"));
            article.setCheckTime(LocalDateTime.now());
            article.setCheckAdmin(currentAdmin.getId().intValue());

            articleService.updateViolationStatus(article);

            return Result.success("标记违规成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("标记失败");
        }
    }

    // 取消违规标记
    @PostMapping("/articles/unmark-violation/{id}")
    public Result<Void> unmarkViolation(@PathVariable Integer id) {
        try {
            AdminUser currentAdmin = getCurrentAdmin();
            if(currentAdmin == null) {
                return Result.error("未登录");
            }

            Article article = articleService.findById(id);
            if (article == null) {
                return Result.error("文章不存在");
            }

            article.setIsViolation(false);
            article.setViolationReason(null);
            article.setCheckTime(LocalDateTime.now());
            article.setCheckAdmin(currentAdmin.getId().intValue());

            articleService.updateViolationStatus(article);

            return Result.success("取消违规标记成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败");
        }
    }


}
