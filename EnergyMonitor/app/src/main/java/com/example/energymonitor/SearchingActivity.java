package com.example.energymonitor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;


/**
 * Created by 김진아 on 2021-08-06.
 */

public class SearchingActivity extends AppCompatActivity {

    //각 시별 도시이름 ( 서울시 -> 종로구, 중구 등)
    String cityURL="https://bigdata.kepco.co.kr/openapi/v1/commonCode.do?codeTy=cityCd&apiKey=F3xhYRDZS4Cej30qb14Rz04fgFLl4109F2v6hyK3";
    String result; //json 가공전

    //스피너
    Spinner spinner_year; //년도별 스피너
    Spinner spinner_month; //월별 스피너
    Spinner spinner_metro; //시도별 스피너
    Spinner spinner_city; //구별 스피너

    Button button_search;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinner_year=(Spinner)findViewById(R.id.spinner_year);
        spinner_month=(Spinner)findViewById(R.id.spinner_month);
        spinner_metro=(Spinner)findViewById(R.id.spinner_metro);
        spinner_city=(Spinner)findViewById(R.id.spinner_city);
        button_search=(Button)findViewById(R.id.button_searching);

        ArrayAdapter yearAdapter=ArrayAdapter.createFromResource(this,R.array.spinner_year,android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter monthAdapter=ArrayAdapter.createFromResource(this,R.array.spinner_month,android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter metroAdapter=ArrayAdapter.createFromResource(this,R.array.spinner_metro,android.R.layout.simple_spinner_dropdown_item);

        setSpinnerAdapter(spinner_year,yearAdapter,1);
        setSpinnerAdapter(spinner_month,monthAdapter,2);
        setSpinnerAdapter(spinner_metro,metroAdapter,3);

        btnEvent();
    }

    //by 김진아, 조회 버튼 이벤트함수
    void btnEvent(){
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),SearchingResultActivity.class);
                startActivity(intent);
            }
        });
    }

    //by 김진아, 스피너 어뎁터 세팅함수
    void setSpinnerAdapter(final Spinner spinner, ArrayAdapter adapter, final int num){

        final String[] str = new String[1];

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //스피너 값 선택함수
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str[0] = (String) spinner.getItemAtPosition(position);

                if(num==1) {
                    str[0] = str[0].substring(0, str[0].length() - 1); //선택연도의 맨 마지막 문자('년') 제거
                    searchingInfo.getInstance().setYear(str[0]); //선택한 연도 저장
                }else if(num==2)  {
                    str[0] = str[0].substring(0, str[0].length() - 1);//선택한 월별 맨 마지막 문자('월') 제거
                    searchingInfo.getInstance().setMonth(str[0]); //선택한 월 저장
                }else if(num==3) {
                    searchingInfo.getInstance().setMetro(str[0]); //선택한 메트로 저장

                    //해당 시에 맞는 도시 스피너 추가
                    try {
                        result= new getJSONResult().execute().get();
                        setJSONData(result);
                    } catch (InterruptedException e) { e.printStackTrace();
                    } catch (ExecutionException e) {e.printStackTrace();}

                }else {
                    searchingInfo.getInstance().setCity(str[0]); //선택한 마을 저장
                    setCodeNum(result); //해당 지역코드 추출
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //by 김진아, 가공전의 json 데이터를 추출하는 함수
    public class getJSONResult extends AsyncTask<String, Void, String>{
        String str,result;
        @Override
        protected String doInBackground(String... params) {
            URL url=null;
            try{
                url=new URL(cityURL);
                HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                if(conn.getResponseCode()==conn.HTTP_OK){
                    InputStreamReader inputStreamReader=new InputStreamReader(conn.getInputStream(),"UTF-8");
                    BufferedReader reader=new BufferedReader(inputStreamReader);
                    StringBuffer buffer=new StringBuffer();
                    while((str=reader.readLine())!=null){
                        buffer.append(str);
                    }
                    result=buffer.toString();
                    reader.close();
                }else System.out.print("통신에러");
            }catch (IOException e){
                e.printStackTrace();
            }
            return result;
        }
    }

    //by 김진아, 선택한 메트로의 마을 추출함수 ex) 서울특별시 -> 중구, 종로구 등
    void setJSONData(String jsonString){
        ArrayList<String> items=new ArrayList<>(); //시도별 구 이름 추가할 리스트
        try{
            JSONArray jsonArray=new JSONObject(jsonString).getJSONArray("data");

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                if(jsonObject.optString("uppoCdNm").equals(searchingInfo.getInstance().getMetro())) {
                    String str=jsonObject.optString("codeNm");
                    if(!str.equals("미분류")) items.add(jsonObject.optString("codeNm"));
                }
            }

            //구 스피너 세팅
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,items);
            setSpinnerAdapter(spinner_city,adapter,4);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //by 김진아, 조회할 지역 공통코드 추출함수 ex) 서울특별시 -> 11번 추출, 중구 -> 12번 추출
    void setCodeNum(String jsonString){
        try{
            JSONArray jsonArray=new JSONObject(jsonString).getJSONArray("data");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);

                if(jsonObject.optString("uppoCdNm").equals(searchingInfo.getInstance().getMetro())) {
                    if (jsonObject.optString("codeNm").equals(searchingInfo.getInstance().getCity())) {
                        searchingInfo.getInstance().setMetroNum(jsonObject.optString("uppoCd"));
                        searchingInfo.getInstance().setCityNum(jsonObject.optString("code"));
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
