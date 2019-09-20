package com.example.asdc.bookdome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.ymsreadbooker.ReadBookActivity;


public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_CHOOSE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        TextView viewById = findViewById(R.id.textView);
        viewById.setOnClickListener(v ->  {
            Intent intent = new Intent(MainActivity.this, ReadBookActivity.class);
            startActivity(intent);

        });




    }


}
