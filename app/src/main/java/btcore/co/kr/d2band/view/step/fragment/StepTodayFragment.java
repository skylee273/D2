package btcore.co.kr.d2band.view.step.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

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
import btcore.co.kr.d2band.databinding.FragmentStepTodayBinding;
import btcore.co.kr.d2band.databinding.FragmentStepWeekBinding;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class StepTodayFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    FragmentStepTodayBinding todayBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        todayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_today, container, false);
        todayBinding.setStepToday(this);

        initChartView();

        mContext = this.getActivity();
        return todayBinding.getRoot();
    }

    private void initChartView(){
        todayBinding.todayChart.setOnChartGestureListener(this);
        todayBinding.todayChart.setOnChartValueSelectedListener(this);
        setData();
        Legend l = todayBinding.todayChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        int color = ContextCompat.getColor(getContext(), R.color.color_chart_xy);
        todayBinding.todayChart.setDescription("");
        todayBinding.todayChart.setNoDataTextDescription("You need to provide data for the chart.");
        todayBinding.todayChart.getAxisLeft().setAxisMinValue(0f);
        todayBinding.todayChart.setDescriptionColor(Color.WHITE);
        todayBinding.todayChart.getAxisLeft().setAxisMaxValue(200f);
        todayBinding.todayChart.getXAxis().setLabelsToSkip(5);
        todayBinding.todayChart.getXAxis().setTextColor(color);
        todayBinding.todayChart.getAxisLeft().setTextColor(color);
        todayBinding.todayChart.getAxisRight().setEnabled(false);
        todayBinding.todayChart.getLegend().setTextColor(Color.WHITE);
        todayBinding.todayChart.animateXY(2000, 2000);
        todayBinding.todayChart.invalidate();
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
        for (int i = 1; i <= 24; i++) {
            String date = String.format("%02d", i);
            xVals.add(String.valueOf(date));
        }
        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 1; i <= 24; i++) {
            yVals.add(new Entry(0, i));
        }
        return yVals;
    }


    private void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;

        set1 = new LineDataSet(yVals, "심박수 선");

        set1.setFillAlpha(110);
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
        dataSets.add(set1);
        LineData data = new LineData(xVals, dataSets);
        todayBinding.todayChart.setData(data);
    }
}