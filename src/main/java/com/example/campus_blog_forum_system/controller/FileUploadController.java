package com.example.campus_blog_forum_system.controller;

import com.example.campus_blog_forum_system.pojo.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
public class FileUploadController
{

    @Value("${app.upload-dir}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result<Void> upload(@RequestParam("file") MultipartFile file) throws IOException {
        //把文件的内容存储到本地磁盘上
        if(file == null || file.isEmpty()) {
            return Result.error("请选择要上传的文件");

        }

        // 2. 文件大小检查
        if (file.getSize() > 10 * 1024 * 1024) {
            return Result.error("文件大小超过限制");
        }

        // 3. 文件类型验证

        //原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !isValidFileType(originalFilename)) {
            return Result.error("不支持的文件类型");
        }


        //文件保存的目录
        String filePath = "E:\\file\\";
        File dir = new File(filePath);
        if(!dir.exists() && !dir.mkdirs()) {
            return Result.error("目录创建失败，请检查权限或者是路径是否正确");
        }

        // 生成随机文件名（保留扩展名）
        String extension = null;
        if (originalFilename != null)
        {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".")) + ".1";
        }
        String filename = UUID.randomUUID() + extension;

        //存储文件
        file.transferTo(new File(filePath + filename));
        return Result.success("url访问地址：http://localhost/files/" + filename);


    }
    private boolean isValidFileType(String filename) {
        String extension = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        Set<String> allowedExtensions = Set.of(".jpg", ".jpeg", ".png", ".gif", ".pdf", ".doc", ".docx");
        return allowedExtensions.contains(extension);
    }
}
