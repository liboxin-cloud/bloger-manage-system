// src/main/java/com/example/campus_blog_forum_system/utils/CaptchaUtil.java
package com.example.campus_blog_forum_system.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

public class CaptchaUtil {

    private static final int WIDTH = 120;
    private static final int HEIGHT = 40;
    private static final int CODE_LENGTH = 4;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    /**
     * 生成验证码图片和文本
     * @return 验证码信息对象
     */
    public static CaptchaInfo generateCaptcha() {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 生成随机验证码
        Random random = new Random();
        StringBuilder captchaText = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            captchaText.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        // 设置字体
        Font font = new Font("Arial", Font.BOLD, 24);
        g.setFont(font);

        // 绘制验证码字符
        for (int i = 0; i < captchaText.length(); i++) {
            g.setColor(new Color(random.nextInt(150), random.nextInt(150), random.nextInt(150)));
            g.drawString(String.valueOf(captchaText.charAt(i)), 20 + i * 20, 28);
        }

        // 添加干扰线
        for (int i = 0; i < 5; i++) {
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            int x1 = random.nextInt(WIDTH);
            int y1 = random.nextInt(HEIGHT);
            int x2 = random.nextInt(WIDTH);
            int y2 = random.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        g.dispose();

        // 转换为Base64
        String base64Image = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            base64Image = Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        CaptchaInfo captchaInfo = new CaptchaInfo();
        captchaInfo.setCode(captchaText.toString());
        captchaInfo.setImageBase64("data:image/png;base64," + base64Image);

        return captchaInfo;
    }

    public static class CaptchaInfo {
        private String code;
        private String imageBase64;

        // getters and setters
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getImageBase64() {
            return imageBase64;
        }

        public void setImageBase64(String imageBase64) {
            this.imageBase64 = imageBase64;
        }
    }
}
