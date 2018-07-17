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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import btcore.co.kr.d2band.R;
import btcore.co.kr.d2band.database.SEVER;
import btcore.co.kr.d2band.databinding.FragmentStepMonthBinding;
import btcore.co.kr.d2band.item.StepItem;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class StepMonthFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener{
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    FragmentStepMonthBinding monthBinding;
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
        int color = ContextCompat.getColor(getContext(), R.color.color_chart_xy);
        monthBinding.monthChart.setDescription("");
        monthBinding.monthChart.setNoDataTextDescription("You need to provide data for the chart.");
        monthBinding.monthChart.getAxisLeft().setAxisMinValue(0f);
        monthBinding.monthChart.setDescriptionColor(Color.WHITE);
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
        for(int i = 0; i <= 31; i++ ){
            String date = String.format("%02d",i);
            xVals.add(String.valueOf(date));
        }

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0; i <= 31; i++ ){
            yVals.add(new Entry(0, i));
        }
        for(int i = 0; i < stepValue.length; i++){
            yVals.set(dateVaule[i], new Entry(stepValue[i], dateVaule[i]));
        }

        return yVals;
    }
    private ArrayList<Entry> setNonYAxisValues() {
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        for(int i = 0; i <= 31; i++ ){
            yVals.add(new Entry(0, i));
        }
        return yVals;
    }
    private boolean checkMonthStep(){
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
    private void setMonth(){
        dateVaule = new int[TodayList.size()];
        stepValue = new int[TodayList.size()];
        for(int i = 0; i < TodayList.size(); i++){
            StepItem item = (StepItem) TodayList.get(i);
            if(item.getDate().contains(getTime())){
                String date [] = item.getDate().split("-");
                dateVaule[i] = Integer.parseInt(date[1]);
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

        if(checkMonthStep()){
            setMonth();
        }

        try {
            if(stepValue.length > 0) {
                yVals = setYAxisValues();
            }else{
                yVals = setNonYAxisValues();
                monthBinding.monthChart.getAxisLeft().setAxisMaxValue(2000f);

            }
        }catch (NullPointerException e){
            yVals = setNonYAxisValues();
            monthBinding.monthChart.getAxisLeft().setAxisMaxValue(2000f);

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
        monthBinding.monthChart.setData(data);

    }
}
