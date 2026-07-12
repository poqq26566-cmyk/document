package your.package.name;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity {

    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        result = findViewById(R.id.result);

        Button button = findViewById(R.id.start);

        button.setOnClickListener(v -> {

            if (!Environment.isExternalStorageManager()) {

                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        Uri.parse("package:" + getPackageName())
                );

                startActivity(intent);

            } else {

                rename();

            }

        });

    }


    private void rename(){

        File dir = Environment
                .getExternalStorageDirectory();


        FileRenamer renamer = new FileRenamer();


        int count = renamer.renameFiles(dir);


        result.setText(
                "完成，共修改 "
                + count
                + " 个文件"
        );

    }

}
