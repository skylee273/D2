package btcore.co.kr.d2band.view.heartrate.presenter;

import btcore.co.kr.d2band.view.heartrate.model.HeartRateModel;

/**
 * Created by leehaneul on 2018-01-17.
 */

/**
 * 뷰에 연결된 것이 아니라 그냥 인터페이스.
 * MVC 가 가진 테스트 가능성 문제와 함께 모듈화 / 유연성 문제 역시 해결 가능하다.
 *
 */
public class HeartRatePresenter implements HeartRate.Presenter {

    HeartRate.View heartRateView;
    HeartRateModel heartRateModel;

    // 생성자 HeartRateActivity 에 뷰를 받아온다.
    public HeartRatePresenter(HeartRate.View heartRateView){

        this.heartRateView = heartRateView;
        this.heartRateModel = new HeartRateModel();
    }


    @Override
    public void initBleData(String[] data) {
        heartRateModel.setHeartRateData(data);
        if(heartRateModel.checkHeartData(data)){
            heartRateView.showHeartData(heartRateModel.makeHeartData());

        }else{
            heartRateView.showErrorMessage("데이터 오류");
        }
    }


}