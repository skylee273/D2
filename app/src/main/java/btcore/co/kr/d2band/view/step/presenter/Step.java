package btcore.co.kr.d2band.view.step.presenter;

import java.util.ArrayList;

/**
 * Created by leehaneul on 2018-01-17.
 */

public interface Step {

    interface view{
        void showTodayData(ArrayList arrayList);
        void showErrorMessage(String message);
    }

    interface Presenter{
        void initBleData(String step, String weight, String height);
    }

}
