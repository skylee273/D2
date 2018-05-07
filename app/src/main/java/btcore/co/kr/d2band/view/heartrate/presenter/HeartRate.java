package btcore.co.kr.d2band.view.heartrate.presenter;

/**
 * Created by leehaneul on 2018-01-17.
 */

/**
 * 액티비티가 뷰 인터페이스를 구현해서 프리젠터가 코드를 만들 인터페이스를
 * 갖도록 하는 것이 좋다. 이렇게 하면 특정 뷰와 결합되지 않고 가상 뷰를 구현해서 간단한 유닛 테스트가 가능하다.
 */
public interface HeartRate {

    interface View{
        void showHeartData(String[] heartRate);
        void showErrorMessage(String message);

    }

    interface Presenter{
        void initBleData(String[] data);
    }

}
