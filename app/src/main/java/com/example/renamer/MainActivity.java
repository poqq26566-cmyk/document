package com.example.renamer;
import android.os.Bundle;
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
        
        Button btn = findViewById(R.id.btn_rename);
        btn.setOnClickListener(v -> {
            File dir = getExternalFilesDir(null);
            if (dir != null) {
                File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    for (File file : files) {
                        String newName = "com.android.system." + new Random().nextInt(1000) + ".cache";
                        file.renameTo(new File(dir, newName));
                    }
                    Toast.makeText(this, "重命名完成", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
