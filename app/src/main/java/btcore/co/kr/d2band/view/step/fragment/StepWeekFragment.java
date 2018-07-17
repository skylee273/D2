package btcore.co.kr.d2band.view.step.fragment;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.database.SEVER;
import btcore.co.kr.d2band.databinding.FragmentStepWeekBinding;
import btcore.co.kr.d2band.item.StepItem;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class StepWeekFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    FragmentStepWeekBinding weekBinding;
    SEVER sever;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM");
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weekBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_step_week, container, false);
        weekBinding.setStepWeek(this);

        initChartView();

        mContext = this.getActivity();
        return weekBinding.getRoot();
    }

    private void initChartView(){
        weekBinding.weekChart.setOnChartGestureListener(this);
        weekBinding.weekChart.setOnChartValueSelectedListener(this);
        // add data
        setData();
        // get the legend (only possible after setting data)
        Legend l = weekBinding.weekChart.getLegend();
        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);
        int color = ContextCompat.getColor(getContext(), R.color.color_chart_xy);
        weekBinding.weekChart.setDescription("");
        weekBinding.weekChart.setNoDataTextDescription("You need to provide data for the chart.");
        weekBinding.weekChart.getAxisLeft().setAxisMinValue(0f);
        weekBinding.weekChart.setDescriptionColor(Color.WHITE);
        weekBinding.weekChart.getXAxis().setTextColor(color);
        weekBinding.weekChart.getAxisLeft().setLabelCount(6, true);
        weekBinding.weekChart.getAxisLeft().setTextColor(color);
        weekBinding.weekChart.getAxisRight().setEnabled(false);
        weekBinding.weekChart.getLegend().setTextColor(Color.WHITE);
        weekBinding.weekChart.animateXY(2000, 2000);
        weekBinding.weekChart.invalidate();
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
        xVals.add("일");
        xVals.add("월");
        xVals.add("화");
        xVals.add("수");
        xVals.add("목");
        xVals.add("금");
        xVals.add("토");

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for(int i = 0; i < 7; i++){
            yVals.add(new Entry(0, i));
        }
        for(int i = 0; i < stepValue.length; i++){
            yVals.set(dateVaule[i], new Entry(stepValue[i], dateVaule[i]));
        }
        return yVals;
    }

    private ArrayList<Entry> setNonYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        yVals.add(new Entry(0, 0));
        yVals.add(new Entry(0, 1));
        yVals.add(new Entry(0, 2));
        yVals.add(new Entry(0, 3));
        yVals.add(new Entry(0, 4));
        yVals.add(new Entry(0, 5));
        yVals.add(new Entry(0, 6));

        return yVals;
    }
    private boolean checkWeekStep(){
        sever = new SEVER();
        try {
            TodayList = sever.getStep();
            if(TodayList.size() > 0 ){
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }
    private void setWeek(){
        dateVaule = new int[TodayList.size()];
        stepValue = new int[TodayList.size()];
        for(int i = 0; i < TodayList.size(); i++){
            StepItem item = (StepItem) TodayList.get(i);
            Calendar serverCalendar = Calendar.getInstance();
            Calendar todayCalendar = Calendar.getInstance();
            String weekDate []  = item.getDate().split("-");

            int year = Integer.parseInt(weekDate[0]);
            int mMonth = Integer.parseInt(weekDate[1]);
            int mDate = Integer.parseInt(weekDate[2]);

            serverCalendar.set(year, mMonth-1, mDate);
            String serverWeek = String.valueOf(serverCalendar.get(Calendar.WEEK_OF_MONTH));
            String todayWeek = String.valueOf(todayCalendar.get(Calendar.WEEK_OF_MONTH));

            if(serverWeek.equals(todayWeek) && item.getDate().contains(getTime())){
                dateVaule[i] = Integer.parseInt(serverWeek);
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

        if(checkWeekStep()){
            setWeek();
        }

        try {
            if(stepValue.length > 0) {
                yVals = setYAxisValues();
            }else{
                yVals = setNonYAxisValues();
                weekBinding.weekChart.getAxisLeft().setAxisMaxValue(2000f);
            }
        }catch (NullPointerException e){
            yVals = setNonYAxisValues();
            weekBinding.weekChart.getAxisLeft().setAxisMaxValue(2000f);
        }

        LineDataSet set1;

        // create a dataset and give it a type
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
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        weekBinding.weekChart.setData(data);

    }
}