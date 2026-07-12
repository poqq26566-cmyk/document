package com.example.renamer;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView result;
    private Button startButton;

    private Uri selectedTreeUri;
    private List<Uri> selectedFileUris;

    private final ActivityResultLauncher<Uri> pickDirLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    return;
                }
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                selectedTreeUri = uri;
                selectedFileUris = null;
                startButton.setEnabled(true);
                DocumentFile picked = DocumentFile.fromTreeUri(this, uri);
                String name = picked != null ? picked.getName() : uri.toString();
                result.setText("已选择文件夹：" + name + "\n点击「开始批量重命名」继续");
            });

    private final ActivityResultLauncher<String[]> pickFilesLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris == null || uris.isEmpty()) {
                    return;
                }
                List<Uri> persisted = new ArrayList<>();
                for (Uri uri : uris) {
                    try {
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                    } catch (SecurityException ignored) {
                    }
                    persisted.add(uri);
                }
                selectedFileUris = persisted;
                selectedTreeUri = null;
                startButton.setEnabled(true);
                result.setText("已选择 " + persisted.size() + " 个文件\n点击「开始批量重命名」继续");
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        startButton = findViewById(R.id.start);
        Button pickDirButton = findViewById(R.id.pick_dir);
        Button pickFilesButton = findViewById(R.id.pick_files);

        pickDirButton.setOnClickListener(v -> pickDirLauncher.launch(null));
        pickFilesButton.setOnClickListener(v -> pickFilesLauncher.launch(new String[]{"*/*"}));

        startButton.setOnClickListener(v -> {
            if (selectedTreeUri == null && (selectedFileUris == null || selectedFileUris.isEmpty())) {
                return;
            }
            confirmAndRename();
        });
    }

    private void confirmAndRename() {
        String scopeDesc = selectedTreeUri != null
                ? "所选文件夹下所有文件"
                : "所选的 " + selectedFileUris.size() + " 个文件";
        new AlertDialog.Builder(this)
                .setTitle("确认批量重命名")
                .setMessage("将把" + scopeDesc + "改成随机名称（保留扩展名）。\n"
                        + "原文件名会记录在 App 内部日志中，可随时查看还原。\n"
                        + "确定要继续吗？")
                .setPositiveButton("确定", (dialog, which) -> rename())
                .setNegativeButton("取消", null)
                .show();
    }

    private void rename() {
        FileRenamer renamer = new FileRenamer(this);
        int count = selectedTreeUri != null
                ? renamer.renameFiles(selectedTreeUri)
                : renamer.renameFiles(selectedFileUris);
        result.setText("完成，共修改 " + count + " 个文件\n（原文件名映射已保存在 App 内部日志）");
    }
}
