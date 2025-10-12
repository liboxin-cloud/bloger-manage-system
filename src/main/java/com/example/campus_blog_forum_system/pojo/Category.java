
package com.example.campus_blog_forum_system.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.groups.Default;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {
    private Integer id; // 主键ID
    @NotEmpty
    private String categoryName; // 分类名称
    @NotEmpty
    private String categoryAlias; // 分类别名
    private Integer createUser; // 创建人ID
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;// 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;// 更新时间

    // 如果说某一个校验项目没有指定分组，默认属于Default分组
    // 分组之间可以继承，A extends B，那么A中拥有B中所有的校验�?
    public interface Add extends Default {

    }

    public interface Update extends Default {

    }
}
