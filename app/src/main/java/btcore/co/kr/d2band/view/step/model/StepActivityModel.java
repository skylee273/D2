package btcore.co.kr.d2band.view.step.model;

import java.util.ArrayList;

import btcore.co.kr.d2band.user.User;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityModel {

    String step;
    String weight, height;
    String mGoal;
    double distance;
    double consumeKal;
    double mileKal;
    User user;
    ArrayList<String> TodayData = new ArrayList<>();

    public StepActivityModel() {
        user = new User();
        weight = user.getWeight();
        height = user.getHeight();
    }

    private void setCalculation() {
        mileKal = 3.7103 + Integer.parseInt(weight) + (0.0359 * (Integer.parseInt(weight) * 60 * 0.0006213) * 2) * Integer.parseInt(weight);
        distance = ((Double.parseDouble(height) - 100) * Double.parseDouble(step)) / 100;
        consumeKal = distance * mileKal * 0.0006213;
    }
    public boolean checkGoal(String goal){
        this.mGoal = goal;
        try{
            if(mGoal.length() > 0){
                return  true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean checkTodayData(String step) {
        try {
            if (step.length() > 0 && weight.length() > 0 && height.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public ArrayList makeTodayData() {
        setCalculation();
        TodayData.clear();
        TodayData.add(step);
        TodayData.add(String.valueOf(consumeKal));
        TodayData.add(String.valueOf(distance));

        return TodayData;
    }


}
