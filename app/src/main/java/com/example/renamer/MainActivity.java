package com.example.renamer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btnRename = findViewById(R.id.btn_rename);
        btnRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取文件目录
                File downloadDir = getExternalFilesDir(null);
                if (downloadDir != null && downloadDir.exists()) {
                    File[] files = downloadDir.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.isFile()) {
                                // 生成随机文件名
                                String newName = "com.android.system." 
                                    + new Random().nextInt(1000) 
                                    + ".cache";
                                File newFile = new File(downloadDir, newName);
                                file.renameTo(newFile);
                            }
                        }
                        Toast.makeText(MainActivity.this, 
                            "重命名完成！", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "没有文件", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
