package com.example.campus_blog_forum_system.except;

import com.example.campus_blog_forum_system.pojo.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e)
    {
        e.printStackTrace();
        return Result.error(StringUtils.hasLength(e.getMessage())? e.getMessage():"操作失败");
    }
    @ExceptionHandler(SQLException.class)
    public Result<Void> handleSQLException(SQLException e)
    {
        return Result.error("数据库操作失败");
    }
}
