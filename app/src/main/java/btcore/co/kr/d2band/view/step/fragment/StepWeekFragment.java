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
import btcore.co.kr.d2band.databinding.FragmentStepWeekBinding;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class StepWeekFragment extends Fragment implements OnChartGestureListener, OnChartValueSelectedListener {
    private final String TAG = getClass().getSimpleName();
    private Context mContext;
    FragmentStepWeekBinding weekBinding;

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
        int color = getResources().getColor(R.color.color_chart_xy);
        weekBinding.weekChart.setDescription("");
        weekBinding.weekChart.setNoDataTextDescription("You need to provide data for the chart.");
        weekBinding.weekChart.getAxisLeft().setAxisMinValue(0f);
        weekBinding.weekChart.setDescriptionColor(Color.WHITE);
        weekBinding.weekChart.getAxisLeft().setAxisMaxValue(200f);

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

        yVals.add(new Entry(0, 0));
        yVals.add(new Entry(0, 1));
        yVals.add(new Entry(0, 2));
        yVals.add(new Entry(0, 3));
        yVals.add(new Entry(0, 4));
        yVals.add(new Entry(0, 5));
        yVals.add(new Entry(0, 6));

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
        weekBinding.weekChart.setData(data);

    }
}