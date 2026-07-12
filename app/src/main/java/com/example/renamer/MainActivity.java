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
    /** 非空表示当前是"从文件夹里精确挑选文件"模式 */
    private List<DocumentFile> selectedFileDocs;
    /** 标记下一次选文件夹后，是要整目录处理还是弹出勾选列表 */
    private boolean pickingForFileMode = false;

    private final ActivityResultLauncher<Uri> pickDirLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocumentTree(), uri -> {
                if (uri == null) {
                    return;
                }
                getContentResolver().takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                );

                if (pickingForFileMode) {
                    showFilePickerDialog(uri);
                } else {
                    selectedTreeUri = uri;
                    selectedFileDocs = null;
                    startButton.setEnabled(true);
                    DocumentFile picked = DocumentFile.fromTreeUri(this, uri);
                    String name = picked != null ? picked.getName() : uri.toString();
                    result.setText("已选择文件夹：" + name + "\n点击「开始批量重命名」继续");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);
        startButton = findViewById(R.id.start);
        Button pickDirButton = findViewById(R.id.pick_dir);
        Button pickFilesButton = findViewById(R.id.pick_files);

        pickDirButton.setOnClickListener(v -> {
            pickingForFileMode = false;
            pickDirLauncher.launch(null);
        });

        pickFilesButton.setOnClickListener(v -> {
            pickingForFileMode = true;
            result.setText("请选择这些文件所在的文件夹（下一步会列出文件供你勾选）");
            pickDirLauncher.launch(null);
        });

        startButton.setOnClickListener(v -> {
            boolean hasTree = selectedTreeUri != null;
            boolean hasFiles = selectedFileDocs != null && !selectedFileDocs.isEmpty();
            if (!hasTree && !hasFiles) {
                return;
            }
            confirmAndRename();
        });
    }

    /** 弹出文件夹内文件的多选列表，供①B精确勾选。 */
    private void showFilePickerDialog(Uri treeUri) {
        DocumentFile dir = DocumentFile.fromTreeUri(this, treeUri);
        if (dir == null) {
            return;
        }
        DocumentFile[] all = dir.listFiles();
        List<DocumentFile> onlyFiles = new ArrayList<>();
        List<String> names = new ArrayList<>();
        for (DocumentFile f : all) {
            if (f.isFile()) {
                onlyFiles.add(f);
                names.add(f.getName());
            }
        }
        if (onlyFiles.isEmpty()) {
            result.setText("这个文件夹里没有可重命名的文件");
            return;
        }

        boolean[] checked = new boolean[onlyFiles.size()];

        new AlertDialog.Builder(this)
                .setTitle("勾选要重命名的文件")
                .setMultiChoiceItems(names.toArray(new String[0]), checked,
                        (dialog, which, isChecked) -> checked[which] = isChecked)
                .setPositiveButton("确定", (dialog, which) -> {
                    List<DocumentFile> picked = new ArrayList<>();
                    for (int i = 0; i < checked.length; i++) {
                        if (checked[i]) {
                            picked.add(onlyFiles.get(i));
                        }
                    }
                    selectedFileDocs = picked;
                    selectedTreeUri = null;
                    startButton.setEnabled(!picked.isEmpty());
                    result.setText("已勾选 " + picked.size() + " 个文件\n点击「开始批量重命名」继续");
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void confirmAndRename() {
        String scopeDesc = selectedTreeUri != null
                ? "所选文件夹下所有文件"
                : "你勾选的 " + selectedFileDocs.size() + " 个文件";
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
                : renamer.renameFiles(selectedFileDocs, true);
        result.setText("完成，共修改 " + count + " 个文件\n（原文件名映射已保存在 App 内部日志）");
    }
}
