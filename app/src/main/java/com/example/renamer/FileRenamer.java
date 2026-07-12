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

public class FileRenamer {

    private final Context context;
    private final SecureRandom random = new SecureRandom();

    public FileRenamer(Context context) {
        this.context = context.getApplicationContext();
    }

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
            extension = oldName.substring(dotIndex);
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
        }
        return success;
    }

    private void appendLog(String oldName, String newName) {
        File logFile = new File(context.getFilesDir(), "rename_log.txt");
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(new Date());
        String line = timestamp + "\t" + oldName + " -> " + newName + "\n";
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(line);
        } catch (IOException ignored) {
        }
    }
}
