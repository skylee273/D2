package btcore.co.kr.d2band.view.step.model;

import java.util.ArrayList;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityModel {

    String step;
    String weight, height;

    double distance;
    double consumeKal;
    double mileKal;

    ArrayList<String> TodayData = new ArrayList<>();

    public void setTodayData(String step, String weight, String height){
        this.step = step;
        this.weight = weight;
        this.height = height;
    }

    private void setCalculation(){
        mileKal = 3.7103 + Integer.parseInt(weight) + (0.0359*(Integer.parseInt(weight)*60*0.0006213)*2)*Integer.parseInt(weight);
        distance =  ((Double.parseDouble(height) - 100)  * Double.parseDouble(step))/100;
        consumeKal = distance * mileKal * 0.0006213;
    }

    public boolean checkTodayData(String step){
        if(step.length() == 0){
            return false;
        }else{
            return  true;
        }
    }

    public ArrayList makeTodayData(){
        TodayData.clear();

        TodayData.add(step);
        TodayData.add(String.valueOf(consumeKal));
        TodayData.add(String.valueOf(distance));

        return TodayData;
    }



}
