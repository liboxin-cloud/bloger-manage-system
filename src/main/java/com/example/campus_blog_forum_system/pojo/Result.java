package com.example.campus_blog_forum_system.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//统一相应结果
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T>
{
    private Integer code;   //状态响应码
    private String message; //提示信息
    private T data;         //相应数据

    //快速返回操作成功响应的结果(附带响应数据)
    public static <T> Result<T> successWithData(T data)
    {
        return new Result<>(0, "success", data);
    }

    public static Result<Void> success(String message)
    {
        return new Result<>(0, message, null);
    }
    //快速返回操作成功结果
    public static Result<Void> success()
    {
        return new Result<>(0, "success", null);
    }

    //返回操作错误的状态吗
    public static Result<Void> error(String message)
    {
        return new Result<>(1, message, null);
    }
    public static <T> Result<T> errorWithData(T data) { return  new Result<> (1, "error", data); }
    public static Result<Void> error()
    {
        return new Result<>(1, "error", null);
    }
}
