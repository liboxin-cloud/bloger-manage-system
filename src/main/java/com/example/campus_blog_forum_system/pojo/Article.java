package com.example.campus_blog_forum_system.pojo;

import com.example.campus_blog_forum_system.anno.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article
{

    private Integer id;             //主键ID
    @NotBlank(message = "标题不能为空")
    @NotEmpty
    @Pattern(regexp = "^\\S{1,20}$")
    private String title;           //文章标题
    @NotEmpty
    private String content;         //文章内容
    @NotEmpty
    @URL
    private String coverImg;        //封面图像
    @State
    private String state;           //文章发布状态 已发布 草稿 高舆情
    @NotNull
    private Integer categoryId;     //文章分类ID
    private Integer createUser;     //创建人ID
    private LocalDateTime createTime;//创建时间
    private LocalDateTime updateTime;//更新时间

    @NotNull
    private Integer creatorType;    //创建者身份0 -- 普通用户， 1 -- 管理员

    private Integer popularity;


    // 添加 getter 和 setter 方法
    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getCoverImg()
    {
        return coverImg;
    }

    public void setCoverImg(String coverImg)
    {
        this.coverImg = coverImg;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public Integer getCategoryId()
    {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId)
    {
        this.categoryId = categoryId;
    }

    public Integer getCreateUser()
    {
        return createUser;
    }

    public void setCreateUser(Integer createUser)
    {
        this.createUser = createUser;
    }

    public LocalDateTime getCreateTime()
    {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime)
    {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime()
    {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime)
    {
        this.updateTime = updateTime;
    }


    public Integer getCreatorType()
    {
        return creatorType;
    }

    public void setCreatorType(Integer creatorType)
    {
        this.creatorType = creatorType;
    }

    public Integer getPopularity()
    {
        return popularity;
    }

    public void setPopularity(Integer popularity)
    {
        this.popularity = popularity;
    }
}
