package btcore.co.kr.d2band.view.step.model;

import btcore.co.kr.d2band.user.User;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityModel {

    private String height;

    public StepActivityModel() {
        User user = new User();
        height = user.getHeight();
    }

    public String getDistance(String step) {
        double distance = ((Double.parseDouble(height) - 100) * Double.parseDouble(step)) / 100;
        return String.valueOf(distance);
    }
    public boolean checkGoal(String goal){
        try{
            return goal.length() > 0;
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean checkStep(String step) {
        try {
            return step.length() > 0 && height.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

}
