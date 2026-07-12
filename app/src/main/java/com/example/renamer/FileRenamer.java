package com.example.renamer;

import java.io.File;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class FileRenamer {
    private static final String[] EXTENSIONS = {
        ".bin", ".dll", ".cache", ".dat", ".tmp", ".bak", 
        ".log", ".sys", ".core", ".ota", ".data", ".idx"
    };
    
    private static final String[] PREFIXES = {
        "com.android.system", "proj", "cache", "unity",
        "Android", "ArcGIS", "FileSystem", "Adobe", "Sqlserver",
        "system", "runtime", "compiler", "render", "update"
    };
    
    private Random random = new SecureRandom();

    public int renameFiles(List<File> files, boolean deleteOriginal) {
        int successCount = 0;
        
        for (File file : files) {
            if (file.exists() && file.isFile()) {
                try {
                    File newFile = generateRandomFile(file.getParentFile());
                    
                    if (file.renameTo(newFile)) {
                        successCount++;
                        if (deleteOriginal && newFile.exists()) {
                            // 原文件已被重命名，实际已删除
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return successCount;
    }

    private File generateRandomFile(File parentDir) {
        String randomName = generateRandomName();
        String extension = EXTENSIONS[random.nextInt(EXTENSIONS.length)];
        
        // 添加时间和随机后缀确保唯一性
        String timestamp = new SimpleDateFormat("MMddHHmmss", Locale.getDefault())
            .format(new Date());
        String uniqueId = String.format("%04d", random.nextInt(10000));
        
        String fileName = randomName + "_" + timestamp + "_" + uniqueId + extension;
        return new File(parentDir, fileName);
    }

    private String generateRandomName() {
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        String suffix = String.format("%02d", random.nextInt(100));
        return prefix + "." + suffix;
    }

    // 支持自定义扩展名
    public String[] getSupportedExtensions() {
        return EXTENSIONS;
    }

    // 生成完全随机的文件名
    public String generateFullRandomName() {
        int length = 8 + random.nextInt(12); // 8-20字符
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
