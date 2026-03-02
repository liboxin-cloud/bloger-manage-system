package com.example.campus_blog_forum_system.pojo;

import lombok.Data;
import java.time.LocalDateTime;


@Data
public class AiAnalysisResult {
    // 文章相关字段
    private Long articleId;              // 文章ID
    private String articleTitle;          // 文章标题
    private String originalText;          // 原文内容（前200字预览）

    // 内容审核结果
    private boolean isPass;                // 是否通过审核（true:合规，false:违规）
    private String conclusion;             // 审核结论（合规/不合规/疑似）
    private String message;                // 审核消息（具体违规内容）

    // 情感分析结果
    private String sentiment;              // 情感倾向（正面/中性/负面）
    private Float sentimentConfidence;     // 情感置信度（0-1之间）
    private Integer sentimentScore;        // 情感得分（可选）

    // 关键词和摘要
    private String keywords;               // 关键词（逗号分隔）
    private String summary;                // 文章摘要

    // 时间戳
    private LocalDateTime analyzeTime;      // 分析时间

    // 扩展字段
    private String extraInfo;               // 额外信息（JSON格式）

    // 无参构造方法
    public AiAnalysisResult() {
        this.analyzeTime = LocalDateTime.now();
    }

    // 带文章信息的构造方法
    public AiAnalysisResult(Long articleId, String articleTitle, String originalText) {
        this.articleId = articleId;
        this.articleTitle = articleTitle;
        this.originalText = originalText;
        this.analyzeTime = LocalDateTime.now();
    }

    // 手动添加 getter/setter（如果不使用 Lombok）
    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentiment() {
        return sentiment;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public Float getSentimentConfidence() {
        return sentimentConfidence;
    }

    public void setSentimentConfidence(Float sentimentConfidence) {
        this.sentimentConfidence = sentimentConfidence;
    }

    public Integer getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Integer sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public LocalDateTime getAnalyzeTime() {
        return analyzeTime;
    }

    public void setAnalyzeTime(LocalDateTime analyzeTime) {
        this.analyzeTime = analyzeTime;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    // 添加一个便捷方法，获取情感标签的样式
    public String getSentimentBadgeClass() {
        if (sentiment == null) return "secondary";
        switch (sentiment) {
            case "正面": return "success";
            case "负面": return "danger";
            case "中性": return "secondary";
            default: return "secondary";
        }
    }

    // 添加一个便捷方法，获取审核状态的样式
    public String getPassBadgeClass() {
        return isPass ? "success" : "danger";
    }

    // 添加一个便捷方法，获取审核状态的文本
    public String getPassText() {
        return isPass ? "合规" : "违规";
    }

    @Override
    public String toString() {
        return "AiAnalysisResult{" +
                "articleId=" + articleId +
                ", articleTitle='" + articleTitle + '\'' +
                ", isPass=" + isPass +
                ", conclusion='" + conclusion + '\'' +
                ", sentiment='" + sentiment + '\'' +
                ", sentimentConfidence=" + sentimentConfidence +
                ", analyzeTime=" + analyzeTime +
                '}';
    }

}
