package btcore.co.kr.d2band.view.step.presenter;

import btcore.co.kr.d2band.view.step.model.StepActivityModel;

/**
 * Created by leehaneul on 2018-01-17.
 */

public class StepActivityPresenter implements Step.Presenter{

    Step.view todayView;
    StepActivityModel todayActivityModel;

    public StepActivityPresenter(Step.view todayView){
        this.todayView = todayView;
        this.todayActivityModel = new StepActivityModel();
    }


    @Override
    public void UpdateStep(String step) {
        if(todayActivityModel.checkTodayData(step)){
            todayView.showTodayData(todayActivityModel.makeTodayData());
        }else{

        }
    }

    @Override
    public void UpdateGoal(String goal) {
        if(todayActivityModel.checkGoal(goal)){
            todayView.showGoal(goal);
        }else{
            todayView.showErrorMessage("목표 설정이 잘못 되었습니다.");
        }
    }
}
