package com.example.energymonitor;

import android.content.Intent;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

/*
created by 김진아, 20210805
 */
public class MainActivity extends AppCompatActivity {

    ImageButton imgBtn_global, imgBtn_searching, imgBtn_co2, imgBtn_electronics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        clickEvent();
    }

    //by 김진아, 초기화 함수
    void init(){
        imgBtn_searching=(ImageButton)findViewById(R.id.imgBtn_searching);
        imgBtn_global=(ImageButton)findViewById(R.id.imgBtn_global);
        imgBtn_co2=(ImageButton)findViewById(R.id.imgBtn_co2);
        imgBtn_electronics=(ImageButton)findViewById(R.id.imgBtn_electronics);
    }

    //by 김진아, 버튼 클릭 이벤트함수
    void clickEvent(){

        //가전제품 통계 버튼
        imgBtn_electronics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplication(),ElectronicsActivity.class);
                startActivity(intent);
            }
        });

        //글로벌 버튼
        imgBtn_global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),GlobalActivity.class);
                startActivity(intent);
            }
        });

        //조회 버튼
        imgBtn_searching.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SearchingActivity.class);
                startActivity(intent);
            }
        });

        imgBtn_co2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),co2Activity.class);
                startActivity(intent);
            }
        });
    }


}
