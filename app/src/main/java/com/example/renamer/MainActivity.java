package com.example.renamer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PERMISSIONS = 100;
    private FileRenamer fileRenamer;
    private FileListAdapter adapter;
    private List<File> selectedFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fileRenamer = new FileRenamer();
        
        Button btnSelectFiles = findViewById(R.id.btn_select_files);
        Button btnRename = findViewById(R.id.btn_rename);
        Button btnDeleteOriginal = findViewById(R.id.btn_delete_original);
        ListView listView = findViewById(R.id.list_files);

        // 请求权限
        requestPermissions();

        btnSelectFiles.setOnClickListener(v -> selectFiles());
        btnRename.setOnClickListener(v -> renameFiles(false));
        btnDeleteOriginal.setOnClickListener(v -> renameFiles(true));
    }

    private void requestPermissions() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
            };
        } else {
            permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }

        List<String> needRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                needRequest.add(permission);
            }
        }

        if (!needRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, 
                needRequest.toArray(new String[0]), 
                REQUEST_CODE_PERMISSIONS);
        }
    }

    private void selectFiles() {
        // 使用文件选择器或直接浏览目录
        File downloadDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);
        File[] files = downloadDir.listFiles();
        
        if (files != null) {
            selectedFiles.clear();
            for (File file : files) {
                if (file.isFile() && file.length() > 1024 * 1024) { // > 1MB
                    selectedFiles.add(file);
                }
            }
            Toast.makeText(this, "已选择 " + selectedFiles.size() + " 个文件", 
                Toast.LENGTH_SHORT).show();
        }
    }

    private void renameFiles(boolean deleteOriginal) {
        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "请先选择文件", Toast.LENGTH_SHORT).show();
            return;
        }

        int successCount = fileRenamer.renameFiles(selectedFiles, deleteOriginal);
        Toast.makeText(this, 
            "成功重命名 " + successCount + " 个文件" + 
            (deleteOriginal ? "，已删除原文件" : ""), 
            Toast.LENGTH_LONG).show();
        
        selectedFiles.clear();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, 
                                           @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            if (!allGranted) {
                Toast.makeText(this, "需要存储权限才能运行", Toast.LENGTH_LONG).show();
            }
        }
    }
}
