package com.example.energymonitor;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

/**
 * Created by 김진아 on 2021-08-07.
 */

//마커뷰 (그래프 클릭하면 나오는 보조 글 상자) 커스텀 클래스
public class CursomMarkerView extends MarkerView {
    private TextView tvContent;
    private ArrayList<String> xLabel;
    private int mode;

    public CursomMarkerView(Context context, int layoutResource, ArrayList<String> xLabel,int mode) {
        super(context, layoutResource);
        tvContent=(TextView)findViewById(R.id.tvContent);
        this.mode=mode;
        this.xLabel=xLabel;
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        if(mode==1) { //co2 막대 그래프 마커뷰
            tvContent.setText((int) e.getX() + "년 " + (int) e.getY());
        }else if(mode==2){ //조회 막대 그래프 마커뷰
            tvContent.setText(xLabel.get((int) e.getX()) + ", " + (int) e.getY()+"kWh");
        }

        super.refreshContent(e,highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth()/2),-getHeight());
    }
}
