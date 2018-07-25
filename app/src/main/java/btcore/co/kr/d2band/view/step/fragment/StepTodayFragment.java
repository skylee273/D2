package btcore.co.kr.d2band.view.step.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.database.ServerCommand;
import btcore.co.kr.d2band.databinding.FragmentStepTodayBinding;
import btcore.co.kr.d2band.item.StepItem;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class StepTodayFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    private final String TAG = getClass().getSimpleName();
    FragmentStepTodayBinding todayBinding;
    ServerCommand serverCommand;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");
    ArrayList TodayList = new ArrayList<StepItem>();
    ArrayList<String> xVals;
    ArrayList<Entry> yVals;
    int [] dateVaule;
    int [] stepValue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        todayBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_today, container, false);
        todayBinding.setStepToday(this);

        initChartView();

        Context mContext = this.getActivity();

        return todayBinding.getRoot();
    }

    private void initChartView(){
        todayBinding.todayChart.setOnChartGestureListener(this);
        todayBinding.todayChart.setOnChartValueSelectedListener(this);
        setData();
        Legend l = todayBinding.todayChart.getLegend();
        l.setForm(Legend.LegendForm.LINE);
        int color = ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.color_chart_xy);
        todayBinding.todayChart.setNoDataTextDescription("You need to provide data for the chart.");
        todayBinding.todayChart.getAxisLeft().setTextColor(color);
        todayBinding.todayChart.getAxisLeft().setAxisMinValue(0f);
        todayBinding.todayChart.getXAxis().setLabelsToSkip(5);
        todayBinding.todayChart.getXAxis().setTextColor(color);
        todayBinding.todayChart.getAxisRight().setEnabled(false);
        todayBinding.todayChart.getAxisRight().setDrawLabels(false);
        todayBinding.todayChart.getAxisRight().setDrawAxisLine(false);
        todayBinding.todayChart.getAxisRight().setDrawGridLines(false);
        todayBinding.todayChart.getLegend().setTextColor(Color.WHITE);
        todayBinding.todayChart.setDrawGridBackground(false);
        todayBinding.todayChart.setDoubleTapToZoomEnabled(false);
        todayBinding.todayChart.setDescription("");
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
        for(int i = 0; i < 24; i++ ){
            @SuppressLint("DefaultLocale")
            String date = String.format("%02d",i);
            xVals.add(String.valueOf(date));
        }
        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0; i < 24; i++){
            yVals.add(new Entry(0, i));
        }
        for(int i = 0; i < stepValue.length; i++){
            yVals.set(dateVaule[i], new Entry(stepValue[i], dateVaule[i]));
        }
        return yVals;
    }


    private ArrayList<Entry> setNonYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for (int i = 0; i < 24; i++) {
            yVals.add(new Entry(0, i));
        }
        return yVals;
    }

    private boolean checkTodayStep(){
        serverCommand = new ServerCommand();
        try {
            TodayList = serverCommand.getStep();
            return TodayList.size() > 0;
        }catch (NullPointerException e){
            return false;
        }
    }

    private void setToday(){
        dateVaule = new int[TodayList.size()];
        stepValue = new int[TodayList.size()];
        for(int i = 0; i < TodayList.size(); i++){
            StepItem item = (StepItem) TodayList.get(i);
            if(item.getDate().contains(getTime())){
                String date [] = item.getDate().split("-");
                dateVaule[i] = Integer.parseInt(date[3]);
                stepValue[i] = Integer.parseInt(item.getStep());
            }
        }
    }
    private String getTime() {
        long mNow;
        Date mDate;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private void setData() {
        xVals = setXAxisValues();

        if(checkTodayStep()){
            setToday();
        }

        try {
            if(stepValue[stepValue.length-1] != 0) {
                yVals = setYAxisValues();
            }else{
                yVals = setNonYAxisValues();
                todayBinding.todayChart.getAxisLeft().setAxisMaxValue(2000f);
            }
        }catch (NullPointerException e){
            yVals = setNonYAxisValues();
            todayBinding.todayChart.getAxisLeft().setAxisMaxValue(2000f);
        }



        LineDataSet set1;

        set1 = new LineDataSet(yVals, "심박수 선");

        set1.setFillAlpha(110);
        set1.setFillColor(Color.parseColor("#F0F5FB"));
        set1.setColor(Color.parseColor("#0B80C9"));
        set1.setCircleColor(Color.parseColor("#FFA1B4DC"));
        set1.setCircleColorHole(Color.BLUE);
        set1.setValueTextColor(Color.WHITE);
        set1.setDrawValues(false);
        set1.setLineWidth(2);
        set1.setCircleRadius(6);
        set1.setDrawCircleHole(false);
        set1.setDrawCircles(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);

        LineData data = new LineData(xVals, dataSets);
        todayBinding.todayChart.setData(data);
    }


}