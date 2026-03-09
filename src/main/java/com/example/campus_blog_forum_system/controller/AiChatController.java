package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Result;
import com.example.campus_blog_forum_system.utils.ThreadLocalUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/ai")
public class AiChatController {

    private final ChatClient chatClient;

    // 简单的会话历史存储（生产环境建议用Redis）
    private final Map<String, List<Message>> sessionHistories = new ConcurrentHashMap<>();

    @Autowired
    public AiChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * 普通问答（一次性返回）
     */
    @PostMapping("/chat")
    public Result<Map<String, String>> chat(@RequestBody ChatRequest request) {
        try {
            String userId = getCurrentUserId();

            // 获取或创建会话历史
            List<Message> history = sessionHistories.computeIfAbsent(userId,
                    k -> {
                        List<Message> list = new ArrayList<>();
                        // 添加系统角色设定
                        list.add(new SystemMessage("你是一个校园论坛的AI助手，可以回答学术问题、提供学习建议、解答校园生活疑问。请用友好、专业的语气回复。"));
                        return list;
                    });

            // 添加用户消息到历史
            history.add(new UserMessage(request.getMessage()));

            // 调用AI
            String response = chatClient.prompt()
                    .messages(history)
                    .call()
                    .content();

            // 添加AI回复到历史
            history.add(new AssistantMessage(response));

            // 限制历史长度（保留最近10轮对话）
            if (history.size() > 21) { // 1条系统消息 + 10轮对话(20条) = 21
                // 保留系统消息和最近10轮
                List<Message> newHistory = new ArrayList<>();
                newHistory.add(history.get(0)); // 系统消息
                newHistory.addAll(history.subList(history.size() - 20, history.size())); // 最近10轮
                sessionHistories.put(userId, newHistory);
            }

            Map<String, String> result = new HashMap<>();
            result.put("response", response);
            return Result.successWithData(result);

        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>();
        }
    }

    /**
     * 流式问答（逐字返回，体验更好）
     */
    @GetMapping(value = "/chat/stream", produces = "text/event-stream;charset=UTF-8")
    public Flux<String> streamChat(@RequestParam String message) {
        String userId = getCurrentUserId();

        // 获取或创建会话历史（同上）
        List<Message> history = sessionHistories.computeIfAbsent(userId,
                k -> {
                    List<Message> list = new ArrayList<>();
                    list.add(new SystemMessage("你是一个校园论坛的AI助手..."));
                    return list;
                });

        history.add(new UserMessage(message));

        // 流式调用
        return chatClient.prompt()
                .messages(history)
                .stream()
                .content()
                .doOnComplete(() -> {
                    // 这里无法直接获取完整回复，需要在客户端处理
                    // 实际项目中可以在完成后保存AI回复到历史
                });
    }

    /**
     * 清除会话历史
     */
    @DeleteMapping("/history")
    public Result<Void> clearHistory() {
        String userId = getCurrentUserId();
        sessionHistories.remove(userId);
        return Result.success("会话已清除");
    }

    /**
     * 获取当前用户ID（从ThreadLocal或session）
     */
    private String getCurrentUserId() {
        try {
            Map<String, Object> claims = ThreadLocalUtil.get();
            if (claims != null && claims.get("id") != null) {
                return claims.get("id").toString();
            }
        } catch (Exception e) {
            // 忽略
        }
        // 未登录用户使用临时ID（实际应根据session）
        return "anonymous_" + System.currentTimeMillis();
    }

    // 请求体类
    public static class ChatRequest {
        private String message;
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}