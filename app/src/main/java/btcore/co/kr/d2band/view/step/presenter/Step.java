package btcore.co.kr.d2band.view.step.presenter;

import java.util.ArrayList;

/**
 * Created by leehaneul on 2018-01-17.
 */

public interface Step {

    interface view{
        void showTodayData(String distance);
        void showErrorMessage(String message);
        void showGoal(String goal);
    }

    interface Presenter{
        void UpdateStep(String step);
        void UpdateGoal(String goal);
    }

}
