package com.example.energymonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

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

public class SearchingResultActivity extends AppCompatActivity {

    String serviceURL; //파싱할 url
    String str_year="", str_month="", str_metro="", str_city="",num_metro="",num_city=""; //조회 정보
    String powerUsage=null; //전력 소비량
    TextView textView_date, textView_region, textView_powerUsage, textView_bill, textView_percentage; // 조회결과창

    BarChart barChart; //통계 막대 그래프
    ArrayList<String> valList=new ArrayList<>(); //막대그래프 : 전력량
    ArrayList<String> labelList=new ArrayList<>(); //막대그래프 : 마을

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        init();

    }

    //by 김진아, 가공전의 json 데이터를 추출하는 함수
    public class getJSONResult extends AsyncTask<String, Void, String>{
        String str,result;
        @Override
        protected String doInBackground(String... params) {
            URL url=null;
            try{
                url=new URL(serviceURL);
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

    //by 김진아, 조회 대상의 전력 정보 추출함수 (가공후)
    void setSearchInfo(String jsonString){
        String bill=null;
        try{
            JSONArray jsonArray=new JSONObject(jsonString).getJSONArray("data");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                powerUsage=jsonObject.optString("powerUsage");
                bill=jsonObject.optString("bill");
            }
            textView_powerUsage.setText(powerUsage+" kWh");
            textView_bill.setText(bill+" 원");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //by 김진아, 통계 그래프를 위한 전력 정보 추출함수 (가공후)
    void setChart(String jsonString){
        try{
            JSONArray jsonArray=new JSONObject(jsonString).getJSONArray("data");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                labelList.add(jsonObject.optString("city"));
                valList.add(jsonObject.optString("powerUsage"));
            }

            BarChartGraph(labelList,valList);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //by 김진아, 저번달과 전력 소비량 비교함수
    void calPowerUsage(String jsonString){
        String lastPower="";
        try{
            JSONArray jsonArray=new JSONObject(jsonString).getJSONArray("data");
            for(int i=0; i<jsonArray.length(); i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                lastPower=jsonObject.optString("powerUsage");
            }
            //저번달 대비 전력 증가율
            float cal=(Float)(Float.parseFloat(powerUsage)-Float.parseFloat(lastPower))/Float.parseFloat(lastPower)*100;
            if(cal>=0) { //양수인경우 + 추가
                String str=String.format("%.2f",cal);
                textView_percentage.setText("+"+str + "%");
                textView_percentage.setTextColor(Color.RED);
            }else{
                String str=String.format("%.2f",cal);
                textView_percentage.setText(str + "%");
                textView_percentage.setTextColor(Color.GREEN);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //by 김진아, 막대그래프 세팅함수
    void BarChartGraph(ArrayList<String> labelList, ArrayList<String> valList){
        //x축 : 데이터값 추가
        ArrayList<BarEntry> entries=new ArrayList<>();
        for(int i=0; i<valList.size(); i++) entries.add(new BarEntry(i, Float.parseFloat(valList.get(i))));

        //x,y축 안보이게
        XAxis xAxis=barChart.getXAxis();
        xAxis.setDrawLabels(false);
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);

        YAxis yAxisRight=barChart.getAxisRight();
        yAxisRight.setDrawLabels(false);

        YAxis yAxisLeft=barChart.getAxisLeft();
        yAxisLeft.setDrawLabels(false);

        BarDataSet depenses = new BarDataSet(entries," ");
        depenses.setColors(ColorTemplate.VORDIPLOM_COLORS);

        BarData data = new BarData(depenses);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        CursomMarkerView markerView=new CursomMarkerView(this.getApplicationContext(),R.layout.custom_marker,labelList,2);
        markerView.setBackgroundColor(Color.DKGRAY); //마커뷰 배경색

        barChart.setData(data);
        barChart.setVisibleXRangeMaximum(15); //최대 15개 그래프 보이게
        barChart.moveViewToX(1); //스크롤
        barChart.setMarker(markerView); //마커뷰 세팅
        barChart.setPinchZoom(false); //줌 불가능
        barChart.setTouchEnabled(true);
        barChart.getXAxis().setDrawGridLines(false); //그래프 격자선 안보이게
        barChart.getLegend().setEnabled(false); //하단의 legend 안보이게
        barChart.setDescription(null); //description 안보이게
        barChart.invalidate();

    }

    //by 김진아, 초기화 함수
    void init(){

        textView_date=(TextView)findViewById(R.id.textView_date);
        textView_region=(TextView)findViewById(R.id.textView_region);
        textView_powerUsage=(TextView)findViewById(R.id.textView_powerUsage);
        textView_bill=(TextView)findViewById(R.id.textView_bill);
        textView_percentage=(TextView)findViewById(R.id.textView_percentage);

        barChart=(BarChart)findViewById(R.id.bar_chart);

        str_year=searchingInfo.getInstance().getYear();
        str_month=searchingInfo.getInstance().getMonth();
        str_metro=searchingInfo.getInstance().getMetro();
        str_city=searchingInfo.getInstance().getCity();
        num_metro=searchingInfo.getInstance().getMetroNum();
        num_city=searchingInfo.getInstance().getCityNum();

        textView_date.setText(str_year+"년 "+str_month+"월");
        textView_region.setText(str_metro+" "+str_city);

        try{

            serviceURL="https://bigdata.kepco.co.kr/openapi/v1/powerUsage/houseAve.do?year="+str_year+"&month="+str_month+"&metroCd="+num_metro+"&cityCd="+num_city+"&apiKey=F3xhYRDZS4Cej30qb14Rz04fgFLl4109F2v6hyK3";
            String resultText=new getJSONResult().execute().get(); //가공안된 조회 지역 전력 사용 정보 json 결과
            setSearchInfo(resultText);

            serviceURL="https://bigdata.kepco.co.kr/openapi/v1/powerUsage/houseAve.do?year="+str_year+"&month="+str_month+"&metroCd="+num_metro+"&apiKey=F3xhYRDZS4Cej30qb14Rz04fgFLl4109F2v6hyK3";
            String resultText2=new getJSONResult().execute().get();
            setChart(resultText2);

            String last_month="",last_year=""; //저번달
            if(str_month.equals("01")) {
                last_year=Integer.toString(Integer.parseInt(str_year)-1);
                last_month="12";
            }else{
                int num=Integer.parseInt(str_month)-1;
                last_year=str_year;
                if(num>=1 && num<=9)last_month="0"+Integer.toString(num);
                else last_month= Integer.toString(num);

            }

            serviceURL = "https://bigdata.kepco.co.kr/openapi/v1/powerUsage/houseAve.do?year=" + last_year + "&month=" + last_month + "&metroCd=" + num_metro + "&cityCd=" + num_city + "&apiKey=F3xhYRDZS4Cej30qb14Rz04fgFLl4109F2v6hyK3";
            String resultText3=new getJSONResult().execute().get();
            calPowerUsage(resultText3);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
