package com.example.renamer;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

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
 * 基于 Storage Access Framework 的批量重命名，支持两种范围：
 * ① 一整个用户授权的目录树（{@link #renameFiles(Uri)}，来自 ACTION_OPEN_DOCUMENT_TREE）；
 * ② 一组具体选中的文件（{@link #renameFiles(List)}，来自 ACTION_OPEN_DOCUMENT 多选）。
 *
 * 标准 DocumentFile#renameTo 在部分来源（尤其是系统选择器"最近"页面选出的 MediaStore 文件）
 * 上不受支持，会直接失败；此时自动降级走 MediaStore 的 DISPLAY_NAME 更新接口再试一次。
 *
 * 每次重命名都会把「原文件名 -> 新文件名」写入 App 私有目录下的
 * {@code rename_log.txt}，方便日后核对或手动还原。
 */
public class FileRenamer {

    private final Context context;
    private final SecureRandom random = new SecureRandom();

    /** 最近一次因缺少 MediaStore 写入授权而失败的异常，Activity 可据此发起授权请求。 */
    public Exception pendingSecurityException;

    public FileRenamer(Context context) {
        this.context = context.getApplicationContext();
    }

    /** 模式①：重命名整个目录树下的一级文件（不递归子目录）。 */
    public int renameFiles(Uri treeUri) {
        if (treeUri == null) {
            return 0;
        }

        DocumentFile dir = DocumentFile.fromTreeUri(context, treeUri);
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return 0;
        }

        int count = 0;
        for (DocumentFile file : dir.listFiles()) {
            if (renameOne(file)) {
                count++;
            }
        }
        return count;
    }

    /** 模式②：只重命名传入的这一批具体文件（来自 ACTION_OPEN_DOCUMENT 多选）。 */
    public int renameFiles(List<Uri> fileUris) {
        if (fileUris == null || fileUris.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Uri uri : fileUris) {
            DocumentFile file = DocumentFile.fromSingleUri(context, uri);
            if (renameOne(file)) {
                count++;
            }
        }
        return count;
    }

    private boolean renameOne(DocumentFile file) {
        if (file == null || !file.isFile()) {
            return false;
        }

        String oldName = file.getName();
        if (oldName == null) {
            return false;
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

        if (!success) {
            // DocumentFile 标准 rename 不支持时（常见于"最近"页面选出的 MediaStore 文件），
            // 降级走 MediaStore 的 DISPLAY_NAME 更新接口再试一次。
            success = renameViaMediaStore(file.getUri(), newName);
        }

        if (success) {
            appendLog(oldName, newName);
        }
        return success;
    }

    private boolean renameViaMediaStore(Uri uri, String newName) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, newName);
            int rows = context.getContentResolver().update(uri, values, null, null);
            return rows > 0;
        } catch (SecurityException e) {
            // API 29+ 上，系统可能要求用户二次授权才能改别的 App 写入的媒体文件，
            // 这里先如实返回失败，具体的授权弹窗流程要在 Activity 层处理。
            pendingSecurityException = e;
            return false;
        } catch (Exception e) {
            return false;
        }
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
