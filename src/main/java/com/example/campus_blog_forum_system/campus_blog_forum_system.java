package com.example.campus_blog_forum_system;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.campus_blog_forum_system.mapper")
public class campus_blog_forum_system
{

    public static void main(String[] args)
    {

        SpringApplication.run(campus_blog_forum_system.class, args);
    }

}
