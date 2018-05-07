package btcore.co.kr.d2band.view.step.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.databinding.FragmentStepMonthBinding;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class StepMonthFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener{
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    FragmentStepMonthBinding monthBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        monthBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_month, container, false);
        monthBinding.setStepMonth(this);

        initChartView();

        mContext = this.getActivity();
        return monthBinding.getRoot();
    }

    private void initChartView(){
        monthBinding.monthChart.setOnChartGestureListener(this);
        monthBinding.monthChart.setOnChartValueSelectedListener(this);
        // add data
        setData();
        // get the legend (only possible after setting data)
        Legend l = monthBinding.monthChart.getLegend();
        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        int color = getResources().getColor(R.color.color_chart_xy);
        monthBinding.monthChart.setDescription("");
        monthBinding.monthChart.setNoDataTextDescription("You need to provide data for the chart.");
        monthBinding.monthChart.getAxisLeft().setAxisMinValue(0f);
        monthBinding.monthChart.setDescriptionColor(Color.WHITE);
        monthBinding.monthChart.getAxisLeft().setAxisMaxValue(200f);

        monthBinding.monthChart.getXAxis().setTextColor(color);
        monthBinding.monthChart.getXAxis().setLabelsToSkip(4);
        monthBinding.monthChart.getAxisLeft().setTextColor(color);
        monthBinding.monthChart.getAxisRight().setEnabled(false);
        monthBinding.monthChart.getLegend().setTextColor(Color.WHITE);
        monthBinding.monthChart.animateXY(2000, 2000);
        monthBinding.monthChart.invalidate();
    }

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

    }

    @Override
    public void onChartLongPressed(MotionEvent me) {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
    private ArrayList<String> setXAxisValues() {
        ArrayList<String> xVals = new ArrayList<String>();
        for(int i = 0; i < 30; i++ ){
            String date = String.format("%02d",i);
            xVals.add(String.valueOf(date));
        }

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0; i < 30; i++ ){
            yVals.add(new Entry(0, i));
        }


        return yVals;
    }


    private void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "심박수 선");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        // set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.WHITE);
        set1.setCircleColor(Color.WHITE);
        set1.setValueTextColor(Color.WHITE);
        set1.setDrawValues(false);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        monthBinding.monthChart.setData(data);

    }
}
