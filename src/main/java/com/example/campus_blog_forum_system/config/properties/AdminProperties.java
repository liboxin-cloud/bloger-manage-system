package com.example.campus_blog_forum_system.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "admin")
public class AdminProperties {
    private Auth auth = new Auth();
    private Upload upload = new Upload();

    @Data
    public static class Auth {
        private boolean enabled;
        private String defaultUsername;
        private String defaultPassword;
    }

    @Data
    public static class Upload {
        private String maxFileSize;
        private List<String> allowedExtensions;
    }
}