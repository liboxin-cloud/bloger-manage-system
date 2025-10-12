package com.example.campus_blog_forum_system.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

// 扫描Mapper接口所在的包（路径必须与AdminMapper的包一致）
@Configuration
@MapperScan("com.example.campus_blog_forum_system.mapper")
public class MyBatisConfig {
}
