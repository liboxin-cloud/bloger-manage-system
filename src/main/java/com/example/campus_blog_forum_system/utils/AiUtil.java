package com.example.campus_blog_forum_system.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.campus_blog_forum_system.config.AI.AiConfig;
import com.example.campus_blog_forum_system.pojo.AiAnalysisResult;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Component
public class AiUtil {

    @Autowired
    private AiConfig aiConfig;

    // 获取Access Token
    private String getAccessToken() throws IOException {
        String url = "https://aip.baidubce.com/oauth/2.0/token";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(
                "grant_type=client_credentials&client_id=" + aiConfig.getApiKey() +
                        "&client_secret=" + aiConfig.getSecretKey(),
                StandardCharsets.UTF_8
        );
        entity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSON.parseObject(result);
            return json.getString("access_token");
        }
    }

    // 文本审核（检测违规内容）
    public AiAnalysisResult textCensor(String text) throws IOException {
        String accessToken = getAccessToken();
        String url = aiConfig.getTextCensorUrl() + "?access_token=" + accessToken;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity("text=" + text, StandardCharsets.UTF_8);
        entity.setContentType("application/x-www-form-urlencoded");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String result = EntityUtils.toString(response.getEntity());
            JSONObject json = JSON.parseObject(result);

            AiAnalysisResult aiResult = new AiAnalysisResult();
            aiResult.setOriginalText(text);

            // 解析审核结果
            if (json.containsKey("conclusion")) {
                String conclusion = json.getString("conclusion");
                aiResult.setPass("合规".equals(conclusion));
                aiResult.setConclusion(conclusion);

                if (json.containsKey("data")) {
                    JSONArray data = json.getJSONArray("data");
                    StringBuilder msg = new StringBuilder();
                    for (int i = 0; i < data.size(); i++) {
                        JSONObject item = data.getJSONObject(i);
                        if (item.containsKey("msg")) {
                            msg.append(item.getString("msg")).append("; ");
                        }
                    }
                    aiResult.setMessage(msg.toString());
                }
            }

            return aiResult;
        }
    }

    // 情感分析
    public JSONObject sentimentAnalysis(String text) throws IOException {
        String accessToken = getAccessToken();
        String url = aiConfig.getSentimentUrl() + "?access_token=" + accessToken;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        Map<String, Object> params = new HashMap<>();
        params.put("text", text);

        StringEntity entity = new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String result = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(result);
        }
    }

    // 评论观点抽取
    public JSONObject commentTag(String text) throws IOException {
        String accessToken = getAccessToken();
        String url = aiConfig.getCommentTagUrl() + "?access_token=" + accessToken;

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        Map<String, Object> params = new HashMap<>();
        params.put("text", text);
        params.put("type", 1);

        StringEntity entity = new StringEntity(JSON.toJSONString(params), StandardCharsets.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);

        try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
            String result = EntityUtils.toString(response.getEntity());
            return JSON.parseObject(result);
        }
    }
}