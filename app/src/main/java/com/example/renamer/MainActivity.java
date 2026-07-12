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

/**
 * 批量重命名工具（仅限用户主动通过系统文件夹选择器授权的目录，例如 Download）。
 *
 * 不申请 MANAGE_EXTERNAL_STORAGE 等敏感权限：改用 Storage Access Framework
 * （ACTION_OPEN_DOCUMENT_TREE），只能访问用户明确选中并授权的那一个文件夹，
 * 不会触碰系统文件或其他目录。
 */
public class MainActivity extends AppCompatActivity {

    private TextView result;
    private Button startButton;
    private Uri selectedTreeUri;

    private final ActivityResultLauncher<Uri> pickDirLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    return;
                }
                // 持久化授权，避免下次启动 App 需要重新选择
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );
                selectedTreeUri = uri;
                startButton.setEnabled(true);
                DocumentFile picked = DocumentFile.fromTreeUri(this, uri);
                String name = picked != null ? picked.getName() : uri.toString();
                result.setText("已选择目录：" + name + "\n点击「开始批量重命名」继续");
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        startButton = findViewById(R.id.start);
        Button pickButton = findViewById(R.id.pick_dir);

        pickButton.setOnClickListener(v -> pickDirLauncher.launch(null));

        startButton.setOnClickListener(v -> {
            if (selectedTreeUri == null) {
                return;
            }
            confirmAndRename();
        });
    }

    private void confirmAndRename() {
        new AlertDialog.Builder(this)
                .setTitle("确认批量重命名")
                .setMessage("将把所选目录下所有文件改成随机名称（保留扩展名）。\n" +
                        "原文件名会记录在 App 内部日志中，可随时查看还原。\n" +
                        "确定要继续吗？")
                .setPositiveButton("确定", (dialog, which) -> rename())
                .setNegativeButton("取消", null)
                .show();
    }

    private void rename() {
        FileRenamer renamer = new FileRenamer(this);
        int count = renamer.renameFiles(selectedTreeUri);
        result.setText("完成，共修改 " + count + " 个文件\n（原文件名映射已保存在 App 内部日志）");
    }
}
