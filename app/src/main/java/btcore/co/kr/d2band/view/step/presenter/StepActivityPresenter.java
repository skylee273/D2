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
    public void initBleData(String step, String weight, String height) {

        todayActivityModel.setTodayData(step, weight, height);
        if(todayActivityModel.checkTodayData(step)){
            todayView.showTodayData(todayActivityModel.makeTodayData());
        }else{
            todayView.showErrorMessage("StepActivityPresenter 데이터 없음" );
        }
    }
}
