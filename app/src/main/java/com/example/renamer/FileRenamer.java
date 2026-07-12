package com.example.renamer;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 基于 Storage Access Framework 的批量重命名。
 *
 * 只操作调用方通过 {@link androidx.activity.result.contract.ActivityResultContracts.OpenDocumentTree}
 * 授权的那一棵目录树（不递归子目录，只处理选中目录下的一级文件），不会触碰其他任何路径。
 *
 * 每次重命名都会把「原文件名 -> 新文件名」写入 App 私有目录下的
 * {@code rename_log.txt}，方便日后核对或手动还原。
 */
public class FileRenamer {

    private final Context context;
    private final SecureRandom random = new SecureRandom();

    public FileRenamer(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * @param treeUri 用户通过系统文件夹选择器授权的目录 Uri
     * @return 成功重命名的文件数量
     */
    public int renameFiles(Uri treeUri) {
        if (treeUri == null) {
            return 0;
        }

        DocumentFile dir = DocumentFile.fromTreeUri(context, treeUri);
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return 0;
        }

        DocumentFile[] files = dir.listFiles();
        int count = 0;

        for (DocumentFile file : files) {
            if (file == null || !file.isFile()) {
                continue;
            }

            String oldName = file.getName();
            if (oldName == null) {
                continue;
            }

            String extension = "";
            int dotIndex = oldName.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < oldName.length() - 1) {
                extension = oldName.substring(dotIndex); // 保留原扩展名，方便相册/文件管理器继续识别
            }

            String newName = "backup_"
                    + System.currentTimeMillis()
                    + "_"
                    + random.nextInt(9999)
                    + extension;

            boolean success;
            try {
                success = file.renameTo(newName);
            } catch (Exception e) {
                success = false;
            }

            if (success) {
                appendLog(oldName, newName);
                count++;
            }
        }

        return count;
    }

    /** 把「原名 -> 新名」追加写入 App 私有目录下的 rename_log.txt，方便日后查询/还原。 */
    private void appendLog(String oldName, String newName) {
        File logFile = new File(context.getFilesDir(), "rename_log.txt");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        String line = timestamp + "\t" + oldName + " -> " + newName + "\n";
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(line);
        } catch (IOException ignored) {
            // 日志写入失败不影响主流程，但会丢失这一条还原记录
        }
    }
}
