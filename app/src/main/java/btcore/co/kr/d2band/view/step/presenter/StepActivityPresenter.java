package btcore.co.kr.d2band.view.step.presenter;

import android.util.Log;

import btcore.co.kr.d2band.view.step.model.StepActivityModel;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityPresenter implements Step.Presenter{

    Step.view stepView;
    StepActivityModel stepActivityModel;

    public StepActivityPresenter(Step.view stepView){
        this.stepView = stepView;
        this.stepActivityModel = new StepActivityModel();
    }


    @Override
    public void UpdateStep(String step) {
        if(stepActivityModel.checkStep(step)){
            stepView.showTodayData(stepActivityModel.getDistance(step));
        }else{
            Log.d("step","null");
        }
    }

    @Override
    public void UpdateGoal(String goal) {
        if(stepActivityModel.checkGoal(goal)){
            stepView.showGoal(goal);
        }else{
            stepView.showErrorMessage("목표 설정이 잘못 되었습니다.");
        }
    }
}
