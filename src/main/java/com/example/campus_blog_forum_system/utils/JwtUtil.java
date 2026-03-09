package com.example.campus_blog_forum_system.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String KEY = "i love china";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 12; // 12小时
    private static final long REFRESH_WINDOW = 1000 * 60 * 30; // 30分钟内可刷新

    /**
     * 生成包含自定义claims的Token
     */
    public static String genToken(Map<String, String> claims) {
        return buildToken(claims, EXPIRATION_TIME);
    }

    /**
     * 生成简化版Token（仅包含用户名和角色）
     */
//    public String genToken(String username, String role) {
//        Map<String, String> claims = new HashMap<>();
//        claims.put("username", username);
//        claims.put("role", role);
//        return genToken(claims);
//    }

    /**
     * 刷新Token（延长有效期）
     */
    public String refreshToken(String token) {
        try {
            // 1. 验证旧Token是否有效
            DecodedJWT oldJwt = JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(cleanToken(token));

            // 2. 检查是否在可刷新时间窗口内
            if (isTokenExpiring(oldJwt)) {
                // 3. 提取原有claims并转换为String类型
                Map<String, Object> claimsObj = oldJwt.getClaim("claims").asMap();
                Map<String, String> claims = new HashMap<>();
                claimsObj.forEach((key, value) -> claims.put(key, value.toString()));

                // 4. 生成新Token（保留原有claims）
                return buildToken(claims, EXPIRATION_TIME);
            }
            throw new RuntimeException("Token已过期，无法刷新");
        } catch (Exception e) {
            System.err.println("刷新Token失败: " + e.getMessage());
            throw new RuntimeException("刷新Token失败", e);
        }
    }

    /**
     * 解析Token返回claims（返回Map<String, Object>）
     */
    public static Map<String, Object> parseToken(String token) {
        try {
            System.out.println("JwtUtil解析token: " + token);
            DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(KEY)).build().verify(token);

            // 直接获取所有claims
            Map<String, Claim> allClaims = decodedJWT.getClaims();
            Map<String, Object> claims = new HashMap<>();

            // 遍历所有claims并转换为Map
            for (Map.Entry<String, Claim> entry : allClaims.entrySet()) {
                String key = entry.getKey();
                Claim claim = entry.getValue();

                // 跳过JWT标准字段
                if (!"exp".equals(key) && !"iat".equals(key) && !"nbf".equals(key)) {
                    if (claim.asString() != null) {
                        claims.put(key, claim.asString());
                    } else if (claim.asLong() != null) {
                        claims.put(key, claim.asLong());
                    } else {
                        // 其他类型处理
                        claims.put(key, claim.asString()); // 默认转为字符串
                    }
                }
            }

            System.out.println("JwtUtil解析到claims: " + claims);
            return claims;
        } catch (Exception e) {
            System.out.println("JwtUtil解析token失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("令牌解析失败", e);
        }
    }

    /**
     * 从Token获取用户名
     */
    public static String getUsernameFromToken(String token) {
        Map<String, Object> claims = parseToken(token);
        return claims != null ? claims.get("username").toString() : null;
    }

    /**
     * 从Token获取角色
     */
    public static String getRoleFromToken(String token) {
        Map<String, Object> claims = parseToken(token);
        return claims != null ? claims.get("role").toString() : null;
    }

    /**
     * 验证Token是否即将过期（在可刷新窗口内）
     */
    private boolean isTokenExpiring(DecodedJWT jwt) {
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt != null &&
                expiresAt.getTime() - System.currentTimeMillis() < REFRESH_WINDOW;
    }

    /**
     * 构建Token的公共方法
     */
    private static String
    buildToken(Map<String, String> claims, long expiration) {
        JWTCreator.Builder builder = JWT.create();

        // 添加claims
        if (claims != null) {
            for (Map.Entry<String, String> entry : claims.entrySet()) {
                builder.withClaim(entry.getKey(), entry.getValue());
            }
        }


        // 设置过期时间
        return builder
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(KEY));
    }

    /**
     * 清理Token字符串
     */
    private static String cleanToken(String token) {
        if (token == null) return null;
        return token.replace("Bearer ", "").trim();
    }

    /**
     * 验证Token有效性
     */
    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC256(KEY))
                    .build()
                    .verify(cleanToken(token));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}