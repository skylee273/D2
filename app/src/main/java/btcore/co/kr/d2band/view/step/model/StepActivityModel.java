package btcore.co.kr.d2band.view.step.model;

import java.util.ArrayList;

import btcore.co.kr.d2band.user.User;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityModel {

    String step;
    String height;
    String mGoal;
    double distance;
    User user;

    public StepActivityModel() {
        user = new User();
        height = user.getHeight();
    }

    public String getDistance(String step) {
        distance = ((Double.parseDouble(height) - 100) * Double.parseDouble(step)) / 100;
        return String.valueOf(distance);
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

    public boolean checkStep(String step) {
        try {
            if (step.length() > 0 && height.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

}
