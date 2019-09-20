package com.example.asdc.bookdome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.ess.filepicker.FilePicker;
import com.ess.filepicker.model.EssFile;
import com.ess.filepicker.util.Const;
import com.example.ymsreadbooker.ReadBookActivity;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_CHOOSE = 1000;
    private TextView viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        viewById = findViewById(R.id.textView);

    }


    public void inputBook(View view) {
        FilePicker
                .from(this)
                .chooseForMimeType()
                .setMaxCount(1)
                .setFileTypes("txt")
                .requestCode(REQUEST_CODE_CHOOSE)
                .start();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) {
                ArrayList<EssFile> essFileList = data.getParcelableArrayListExtra(Const.EXTRA_RESULT_SELECTION);

                LogUtils.e("文件:"+essFileList);

                viewById.setText(essFileList.get(0).getName());

                viewById.setOnClickListener(v ->  {
                    ReadBookActivity.getInstance(MainActivity.this,essFileList.get(0).getAbsolutePath());

                });
            }
        }

    }
}
