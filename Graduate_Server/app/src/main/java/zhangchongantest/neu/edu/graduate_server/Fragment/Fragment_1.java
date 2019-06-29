package zhangchongantest.neu.edu.graduate_server.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import zhangchongantest.neu.edu.graduate_server.Activity.MainActivity;
import zhangchongantest.neu.edu.graduate_server.Config;
import zhangchongantest.neu.edu.graduate_server.DataBase.DataBaseControl;
import zhangchongantest.neu.edu.graduate_server.ObjectConfig;
import zhangchongantest.neu.edu.graduate_server.R;
import zhangchongantest.neu.edu.graduate_server.SocketConnect.ConnectManager;

import static zhangchongantest.neu.edu.graduate_server.R.id.lineChart;

/**
 * Created by Cheung SzeOn on 2019/4/9.
 */

public class Fragment_1 extends BaseFragment{
    private Boolean isPrepared;
    private LineChart mLineChart;
    private Context mContext;
    public Fragment_1(){
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_1_layout,null);
        initView(view);
        isPrepared = true;
        return view;
    }

    private void initView(View view){
        mLineChart= (LineChart)view.findViewById(lineChart);
        initChartLine(mLineChart);
//        // 是否绘制背景颜色。
//        // 如果mLineChart.setDrawGridBackground(false)，
//        // 那么mLineChart.setGridBackgroundColor(Color.CYAN)将失效;
//        lineChart.setDrawGridBackground(false);
//        lineChart.setGridBackgroundColor(Color.CYAN);
//        // 触摸
//        lineChart.setTouchEnabled(true);
//        // 拖拽
//        lineChart.setDragEnabled(true);
//        // 缩放
//        lineChart.setScaleEnabled(true);
//        //both x and y axis can be scaled simultaneously with 2 fingers
//        lineChart.setPinchZoom(false);
//        lineChart.getLegend().setEnabled(false);
//        lineChart.setData(getLineData());
//        lineChart.setDescription("");
//        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
//            @Override
//            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//                Toast.makeText(context,e.getVal()+"",Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onNothingSelected() {
//
//            }
//        });
    }

    private void initChartLine(LineChart lineChart){
        //border
        lineChart.setDrawBorders(true);
        lineChart.setTouchEnabled(true);
        // 拖拽
        lineChart.setDragEnabled(true);
        // 缩放
        lineChart.setScaleEnabled(true);
        //both x and y axis can be scaled simultaneously with 2 fingers
        lineChart.setPinchZoom(false);
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview
        lineChart.setNoDataTextDescription("如果传给MPAndroidChart的数据为空，那么你将看到这段文字@dialog");
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        YAxis leftYAxis = lineChart.getAxisLeft();
        YAxis rightYAxis = lineChart.getAxisRight();
        leftYAxis.setLabelCount(5,false);
        rightYAxis.setLabelCount(5,false);
        leftYAxis.setSpaceBottom(5f);
        leftYAxis.setSpaceTop(100f);
        leftYAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return (int)value+"元";
            }
        });
        rightYAxis.setSpaceBottom(5f);
        rightYAxis.setSpaceTop(100f);
        rightYAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return (int)value+"元";
            }
        });
        Legend legend = lineChart.getLegend();
        legend.setEnabled(true);// icon
        legend.setTextColor(Color.CYAN); //设置Legend 文本颜色
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);

    }
    List<String> xline = new ArrayList<>();
    private LineData getLineData(Map<String,Integer> map){
//        List<Entry> xLine=new ArrayList<>();
//        List<Entry> line1=new ArrayList<>();
//        List<String> x=new ArrayList<>();
//        for (int i=0;i<10;i++){
//            xLine.add(new Entry(i,i*10));
//            line1.add(new Entry((float) (Math.random() *i+20),i));
//            x.add("x:"+i);
//        }
//        LineDataSet set=new LineDataSet(xLine,"");
//        // 设置mLineDataSet.setDrawHighlightIndicators(false)后，
//        // Highlight的十字交叉的纵横线将不会显示，
//        // 同时，mLineDataSet.setHighLightColor(Color.CYAN)失效。
//        set.setDrawHighlightIndicators(false);
//        // 按击后，十字交叉线的颜色
////        set.setHighLightColor(Color.CYAN);
//        //设置线条的颜色
//        set.setColor(Color.rgb(183, 56, 63));
//        //设置点(小圆点的颜色)
//        set.setCircleColor(Color.rgb(247, 85, 47));
//
//        LineData data=new LineData(x,set);
        List<Entry> data = new ArrayList<>();

        int index = 0;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            data.add(new Entry((float)entry.getValue(),index));
            if (!xline.contains(entry.getKey())) {
                xline.add(entry.getKey());
            }
            Log.e(Config.TAG,"KEY:"+entry.getKey()+"*****VALUE:"+(float)entry.getValue());
            Log.e(Config.TAG,"data:"+data.size()+"*****xline:"+xline.size());
            ++index;
        }
        LineDataSet lineDataSet = new LineDataSet(data,"营业额");
        lineDataSet.setDrawCircleHole(false);
        //设置显示值的字体大小
        lineDataSet.setValueTextSize(9f);
        //线模式为圆滑曲线（默认折线）
        lineDataSet.setDrawCubic(true);
        LineData lineData = new LineData(xline,lineDataSet);
        return lineData;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible){
            return;
        }
        Map<String,Integer> map=DataBaseControl.updatedBenifitChart();
        mLineChart.setData(getLineData(map));
    }
}
