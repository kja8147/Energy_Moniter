package com.example.energymonitor;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * Created by 김진아 on 2021-08-10.
 */

public class co2Activity extends AppCompatActivity {

    InputStream co2InputStream= null; //에너지 파일 inputstream
    Workbook co2Workbook=null; //에너지 파일 workbook

    Spinner spinner_continents=null; //대륙 스피너
    Spinner spinner_countries=null; //나라 스피너

    ArrayList<ArrayList<String>> co2Result=new ArrayList<>(); //시대별로 1~12위 co2 배출량 나라순으로 저장한 배열
    ArrayList<Entry> oecdData = new ArrayList<Entry>(); //oecd 총 co2 배출량
    ArrayList<BarEntry> worldData=new ArrayList<BarEntry>(); //world 총 co2 배출량
    ArrayList<BarEntry> countryData=new ArrayList<>(); //나라 총 co2 배출량

    private CombinedChart combinedChart; //막대 + 선 그래프
    private PieChart pieChart; //원 그래프
    private BarChart barChart; //막대 그래프
    private final int itemcount = 31; //1990~2020
    private TextView textView_clickYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_co2);

        init(); //초기화 함수
        readExel(); //엑셀 파일 읽기
        setCombineChart(); //컴바인 차트 생성
        setPieChart(30,pieChart,new ArrayList<PieEntry>()); //원 그래프 생성
    }


    //by 김진아, 원 그래프 세팅함수 (나라이름, co2 배출량)
    private void setPieChart( int xIndex, final PieChart pieChart , ArrayList<PieEntry> entries){

        //데이터 저장
        //co2Result 형태 ( 나라이름:co2 배출량)
        for (int i = 0; i < co2Result.get(xIndex).size(); i++) {
            String[] info = co2Result.get(xIndex).get(i).split(":"); //토큰분리
            entries.add(new PieEntry(Integer.parseInt(info[1]),info[0]));  //배출량 entries
        }

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                pieChart.setCenterText((int)e.getY()+" MtCO2"); //그래프 누르는 값에 따라 가운데 텍스트 달라짐
            }

            @Override
            public void onNothingSelected() {}
        });

        PieDataSet dataSet=new PieDataSet(entries," ");
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setUsingSliceColorAsValueLineColor(true);

        PieData data=new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(15f); //텍스트 크기
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        pieChart.setMinAngleForSlices(40);
        pieChart.setUsePercentValues(true); //퍼센티지 사용 x -> 원본데이터 출력
        pieChart.setDrawHoleEnabled(true); //가운데 뚫을건지
        pieChart.setDescription(null);
        pieChart.setEntryLabelColor(Color.BLACK); //라벨 텍스트 컬러
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD); //라벨 볼드 처리
        pieChart.setHoleRadius(45f); //가운데 지름
        pieChart.setTransparentCircleRadius(50f); //더 안쪽 지름
        pieChart.setCenterTextSize(20f); //가운데 텍스트 크기
        pieChart.setDrawCenterText(true); //가운데 텍스트 쓸건지
        pieChart.setData(data);
        pieChart.animateXY(1000,1000);
        pieChart.invalidate();
    }

    //by 김진아, 막대 그래프 세팅함수
    private void setBarChart(BarChart barChart,ArrayList<BarEntry> entries){

        YAxis rightAxis = barChart.getAxisRight(); //오른쪽 y축
        rightAxis.setDrawLabels(false); //오른쪽 draw false

        YAxis leftAxis = barChart.getAxisLeft(); //왼쪽 y축
        leftAxis.setDrawLabels(false); // left y labels draw false

        XAxis xAxis = barChart.getXAxis(); //x 축
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //위치 아래
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);

        CursomMarkerView markerView=new CursomMarkerView(this,R.layout.custom_marker,new ArrayList<String>(),1);
        markerView.setBackgroundColor(Color.DKGRAY); //마커뷰 배경색

        BarDataSet dataSet=new BarDataSet(entries," ");
        BarData data=new BarData(dataSet);
        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        dataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        barChart.setDescription(null);
        barChart.setData(data);
        barChart.animateY(2000);
        barChart.getXAxis().setDrawGridLines(false); //그래프 격자선 안보이게
        barChart.getLegend().setEnabled(false); //하단의 legend 안보이게
        barChart.setVisibleXRangeMaximum(10); //최대 10개 그래프 보이게
        barChart.moveViewToX(1); //스크롤
        barChart.setMarker(markerView); //마커뷰 세팅
        barChart.invalidate();

    }

    //by 김진아, 컴바인 차트 ( 막대 + 선 그래프) 생성함수
    private void setCombineChart(){
        combinedChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                textView_clickYear.setText((int)e.getX()+"년");
                setPieChart((int) e.getX()-1990,pieChart,new ArrayList<PieEntry>()); //원 차트 그리기
            }

            @Override
            public void onNothingSelected() {}
        });

        // draw bars behind lines
        combinedChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE
        });

        YAxis rightAxis = combinedChart.getAxisRight(); //오른쪽 y축
        rightAxis.setDrawLabels(false); //오른쪽 draw false

        YAxis leftAxis = combinedChart.getAxisLeft(); //왼쪽 y축
        leftAxis.setDrawLabels(false); // left y labels draw false

        XAxis xAxis = combinedChart.getXAxis(); //x 축
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); //위치 아래
        xAxis.setTypeface(Typeface.DEFAULT_BOLD);

        CombinedData data = new CombinedData(); //콤바인 차트 생성 (x축)
        data.setValueTypeface(Typeface.DEFAULT_BOLD);

        BarDataSet barDataSet = new BarDataSet(worldData, "world"); //world dataset
        barDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);

        BarData barData = new BarData(barDataSet); //막대 그래프 데이터 생성
        barData.setValueTypeface(Typeface.DEFAULT_BOLD);

        LineDataSet lineDataSet = new LineDataSet(oecdData, "oecd"); //set oecd 설정
        LineData lineData = new LineData(lineDataSet); //선 그래프 데이터 생성
        lineData.setValueTypeface(Typeface.DEFAULT_BOLD);

        data.setData(barData); //막대그래프 추가
        data.setData(lineData); //선그래프 추가

        combinedChart.setDescription(null);
        combinedChart.setDrawGridBackground(false); //격자 안보이게
        combinedChart.setData(data); //차트에 대입
        combinedChart.setVisibleXRangeMaximum(10); //최대 10개 그래프 보이게
        combinedChart.getXAxis().setDrawGridLines(false); //그래프 격자선 안보이게
        combinedChart.moveViewToX(1); //스크롤
        combinedChart.animateXY(1000,1000);
        combinedChart.invalidate();
    }

    //by 김진아, 엑셀 데이터 읽는 함수
    void readExel(){
        if(co2Workbook!=null){ //co2 파일 있으면
            Sheet sheet=co2Workbook.getSheet(0); //시트 불러옴
            if(sheet!=null){
                //world 전기 소비량
                for(int col=1; col<=itemcount; col++){
                    String world=sheet.getCell(col,3).getContents(); //world 총합 사용량
                    String oecd=sheet.getCell(col,4).getContents(); //oecd 총합 사용량

                    world = world.substring(0, world.length() - 1);
                    oecd = oecd.substring(0, oecd.length() - 1);
                    worldData.add(new BarEntry(1989+col,Integer.parseInt(world)));
                    oecdData.add(new Entry(1989+col,Integer.parseInt(oecd)));

                    ArrayList<String> arrayList=new ArrayList<>(); //순위별로 전력 사용량 나눈 배열 : col(b~af) , row(62~73)
                    for(int row=61; row<=70; row++) arrayList.add(sheet.getCell(col,row).getContents());
                    co2Result.add(arrayList);
                }

            }
        }
    }

    //해당 나라의 시대별 전력 소비량 읽는 함수
    void readCountryExel(String country){
        if(co2Workbook!=null){ //에너지 파일 있으면
            Sheet sheet=co2Workbook.getSheet(0); //시트 불러옴
            if(sheet!=null){
                countryData.clear();
                for(int row=9; row<=62; row++){
                    if(sheet.getCell(0,row).getContents().equals(country)){
                        for(int col=1; col<=itemcount; col++){
                            String data=sheet.getCell(col,row).getContents();
                            data = data.substring(0, data.length() - 1);
                            countryData.add(new BarEntry(1989+col,Integer.parseInt(data)));
                        }
                        break;
                    }
                }

            }
        }
    }

    //by 김진아, 스피너 어뎁터 세팅함수 : order==1 -> 대륙 스피너 , 2-> 나라 스피너
    void setSpinnerAdapter(final Spinner spinner, ArrayAdapter adapter, final int order){

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //클릭함수
            @Override
            //스피너 값 선택함수
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(order==1) {//대륙 스피너 선택한 경우 나라 스피너 세팅.

                    int pos = spinner.getSelectedItemPosition(); //선택 인덱스
                    ArrayAdapter countryAdapter = null; //선택한 나라 어뎁터

                    if (pos == 0) { //유럽 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_Europe, android.R.layout.simple_spinner_dropdown_item);
                    } else if (pos == 1) { // cis 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_CIS, android.R.layout.simple_spinner_dropdown_item);
                    } else if (pos == 2) { // 아메리카 스피너아시아 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_America, android.R.layout.simple_spinner_dropdown_item);
                    } else if (pos == 3) { //아시아 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_Asia, android.R.layout.simple_spinner_dropdown_item);
                    } else if (pos == 4) { //퍼시픽 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_Pacific, android.R.layout.simple_spinner_dropdown_item);
                    } else if (pos == 5) { //아프리카 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_Africa, android.R.layout.simple_spinner_dropdown_item);
                    } else { //중동 스피너
                        countryAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.spinner_MiddleEast, android.R.layout.simple_spinner_dropdown_item);
                    }
                    setSpinnerAdapter(spinner_countries, countryAdapter, 2);

                }else{ //나라 스피너 선택한경우 해당 나라 정보차트 그림
                    readCountryExel((String) spinner.getItemAtPosition(position));
                    setBarChart(barChart,countryData);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    //by 김진아, 초기화 함수
    void init(){
        spinner_continents=(Spinner)findViewById(R.id.spinner_co2_continents);
        spinner_countries=(Spinner)findViewById(R.id.spinner_co2_countries);
        textView_clickYear=(TextView)findViewById(R.id.textView_co2_year);
        combinedChart = (CombinedChart) findViewById(R.id.co2_chart1);
        pieChart=(PieChart)findViewById(R.id.co2_chart2);
        barChart=(BarChart)findViewById(R.id.co2_chart3);

        //대륙 스피너
        ArrayAdapter continentsAdapter=ArrayAdapter.createFromResource(this,R.array.spinner_continents,android.R.layout.simple_spinner_dropdown_item);
        setSpinnerAdapter(spinner_continents,continentsAdapter,1);

        try {
            co2InputStream = getBaseContext().getResources().getAssets().open("global_co2.xls"); //co2 엑셀파일
            co2Workbook=Workbook.getWorkbook(co2InputStream); //엑셀시트
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }

    }
}
