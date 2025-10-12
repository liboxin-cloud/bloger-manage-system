package com.example.campus_blog_forum_system.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Data

public class Comment {
    private Integer commentId;
    private Integer articleId;
    private Integer userId;
    private String content;
    private Integer parentId;
    private LocalDateTime createdTime;
    private LocalDateTime updateTime;
    public Comment() {}
    public Comment(String content,
                   Integer parentId,
                   Integer articleId,
                   Integer userId,
                   LocalDateTime createdTime,
                   LocalDateTime updateTime) {
        this.content = content;
        this.parentId = parentId;
        this.articleId = articleId;
        this.userId = userId;
        this.createdTime = createdTime;
        this.updateTime = updateTime;
    }
}
