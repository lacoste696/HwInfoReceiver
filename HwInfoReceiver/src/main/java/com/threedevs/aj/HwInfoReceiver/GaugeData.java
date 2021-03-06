package com.threedevs.aj.HwInfoReceiver;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.format;

/**
 * Created by AJ on 26.12.2014.
 */
public class GaugeData {
    private double value_current = 0.0;
    private double value_max = 0.0;
    private double value_min = 0.0;

    private boolean first_value_set = false;
    private boolean first_value_set_gauge = false;

    private boolean auto_adjust_scale = false;

    private int precision = 5;

    private String name = "loading/long press";
    private String unit = "";

    //colors...
    public static int color_graph_line = Color.argb(0xFF, 0x4C, 0xAF, 0x50);
    public static int color_graph_cirlce = Color.argb(0xFF, 0x66, 0xBB, 0x6A);
    public static int color_graph_fill = Color.argb(0xFF, 0x11, 0xAA, 0x11);

    public static int color_graph_min = Color.argb(0xFF, 0x41, 0x69, 0xE1);
    public static int color_graph_max = Color.argb(0xFF, 0xB2, 0x22, 0x22);
    public static int color_graph_cur = Color.argb(0xFF, 0xD4, 0x55, 0x00);

    public static final int TYPE_GAUGE = 1;
    public static final int TYPE_GRAPH = 2;

    private int type = GaugeData.TYPE_GRAPH;

    //graph data...
    private ArrayList<Double> values = new ArrayList<Double>();
    private int max_value_count = 45;

    //if it's a gauge
    private CustomGauge gauge_view;
    private TextView name_view;
    private TextView cur_view;
    private TextView min_view;
    private TextView max_view;

    //if it's a graph
    private LineChart graph_view;

    public GaugeData(){

    }

    public void updateViews(){
        if(type == TYPE_GAUGE) {
            if (gauge_view != null) {
                gauge_view.setTitle(unit);

                if (auto_adjust_scale && first_value_set) {
                    gauge_view.setMaxValue((float) value_max);
                    gauge_view.setMinValue((float) value_min);
                }
                gauge_view.setValue((float) value_current);
                first_value_set_gauge = true;
                gauge_view.update();
            }

            if (name_view != null) {
                name_view.setText(name);
            }
            if (cur_view != null) {
                cur_view.setText("Cur: " + format("%." + precision + "f", value_current));
            }
            if (min_view != null) {
                min_view.setText("Min: " + format("%." + precision + "f", value_min));
            }
            if (max_view != null) {
                max_view.setText("Max: " + format("%." + precision + "f", value_max));
            }
        }
        else if(type == TYPE_GRAPH){
            //graph_view.invalidate();
        }
    }

    public void setAutoAdjustScale(boolean adjust){
        auto_adjust_scale = adjust;
    }

    public void setGraphView(LineChart g){
        first_value_set = false;
        graph_view = g;
    }

    public void setGaugeView(CustomGauge g){
        gauge_view = g;
    }

    public void setTitleTextView(TextView tv){
        name_view = tv;
    }

    public void setCurrentTextView(TextView tv){
        cur_view = tv;
    }

    public void setMinTextView(TextView tv){
        min_view = tv;
    }

    public void setMaxTextView(TextView tv){
        max_view = tv;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public void setUnit(String unit){
        this.unit = unit;
        if(unit.equals("%")){
            precision = 1;
        }
        else if(unit.equals("°C")){
            precision = 1;
        }
        else if(unit.equals("MB")){
            precision = 1;
        }
        else if(unit.equals("V")){
            precision = 6;
        }
        else if(unit.equals("MHz")){
            precision = 2;
        }
        else if(unit.equals("Yes/No")){
            precision = 0;
        }
        else if(unit.equals("W")){
            precision = 6;
        }
        else if(unit.equals("RPM")){
            precision = 1;
        }
        else if(unit.equals("MB/s")){
            precision = 6;
        }
        else if(unit.equals("% of TDP")){
            precision = 3;
        }
        else if(unit.equals("Gbps")){
            precision = 1;
        }
        else if(unit.equals("x")){
            precision = 0;
        }
        else{
            precision = 3;
        }
    }

    public String getUnit(){
        return unit;
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, name + " " + unit);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(color_graph_line);
        set.setCircleColor(color_graph_line);
        set.setCircleHoleColor(color_graph_line);
        set.setLineWidth(2f);
        set.setCircleRadius(1f);
        //set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(10f);
        set.setDrawCircles(true);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setFillColor(color_graph_fill);
        set.setFillAlpha(32);
        return set;
    }

    public void setValue(double value){
        value_current = value;

        values.add(value);
        if(values.size() > max_value_count){
            values.remove(0);
        }

        //Log.i("setValue", "value_current: " + value_current);

        if(type == TYPE_GAUGE) { //otherwise graph doesnt get drawn it's first value and max/min
            if (first_value_set == false) {
                first_value_set = true;
                value_max = value;
                value_min = value;
                return;
            }
        }
        value_max = Math.max(value_max,value);
        value_min = Math.min(value_min,value);

        if(type == TYPE_GRAPH){
            if(graph_view != null) {
                LineData data = graph_view.getData();
                if (data != null) {
                    ILineDataSet set = data.getDataSetByIndex(0);
                    if(set == null) {
                        set = createSet();
                        data.addDataSet(set);
                    }

                    set.setLabel(name + " " + unit);

                    if (first_value_set == false) {
                        first_value_set = true;

                        for(int i = 0; i < values.size(); i++){
                            data.addEntry(new Entry(set.getEntryCount(), values.get(i).floatValue()), 0);
                        }
                    }

                    data.addEntry(new Entry(set.getEntryCount(), (float)value_current), 0);


                    YAxis leftAxis = graph_view.getAxisLeft();

                    leftAxis.setAxisMinimum((float)value_min);
                    leftAxis.setAxisMaximum((float)value_max);

                    LimitLine llcur = new LimitLine((float)value_current,  (int)value_current + "");
                    llcur.setLineWidth(1f);
                    llcur.setLineColor(color_graph_cur);
                    if(value_max - value_current < value_current - value_min) {
                        llcur.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                    }
                    else{
                        llcur.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                    }
                    llcur.setTextSize(8f);
                    llcur.setTextColor(color_graph_cur);

                    LimitLine llmin = new LimitLine((float)value_min,  (int)value_min + "");
                    llmin.setLineWidth(1f);
                    llmin.setLineColor(color_graph_min);
                    llmin.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
                    llmin.setTextSize(8f);
                    llmin.setTextColor(color_graph_min);

                    LimitLine llmax = new LimitLine((float)value_max,  (int)value_max + "");
                    llmax.setLineWidth(1f);
                    llmax.setLineColor(color_graph_max);
                    llmax.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
                    llmax.setTextSize(8f);
                    llmax.setTextColor(color_graph_max);
                    // reset all limit lines to avoid overlapping lines
                    leftAxis.removeAllLimitLines();
                    leftAxis.addLimitLine(llcur);
                    leftAxis.addLimitLine(llmin);
                    leftAxis.addLimitLine(llmax);

                    // let the chart know it's data has changed
                    data.notifyDataChanged();
                    graph_view.notifyDataSetChanged();
                    //graph_view.invalidate();

                    // limit the number of visible entries
                    graph_view.setVisibleXRangeMaximum(45);

                    // move to the latest entry
                    graph_view.moveViewToX(data.getEntryCount());
                }
            }
        }

        if(type == TYPE_GAUGE){
            updateViews();
        }
    }

    public double getValue(){
        return value_current;
    }

    public double getValueMin(){
        return value_min;
    }

    public double getValueMax(){
        return value_max;
    }

    public void setValueMin(double value){
        value_min = value;
    }

    public void setValueMax(double value){
        value_max = value;
    }

    public int getPrecision(){
        return precision;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
         this.type = type;
    }

}
