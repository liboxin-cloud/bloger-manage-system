package com.example.campus_blog_forum_system.config.AI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai.baidu")
public class AiConfig {
    private String apiKey;
    private String secretKey;
    private String textCensorUrl = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
    private String sentimentUrl = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify";
    private String commentTagUrl = "https://aip.baidubce.com/rpc/2.0/nlp/v1/comment_tag";

    // Getters and Setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    public String getTextCensorUrl() { return textCensorUrl; }
    public String getSentimentUrl() { return sentimentUrl; }
    public String getCommentTagUrl() { return commentTagUrl; }
}