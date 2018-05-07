package btcore.co.kr.d2band.view.main;

/**
 * Created by leehaneul on 2018-02-20.
 */

public interface Main {
    interface View{
        void showErrorMessage(String msg);
        void startRequsetSelectDevice();
        void startMainActivity();
        void startRequestEnableBt();
    }
    interface Presenter{
        void checkBluetooth();
        void isEnabled();
        void isNext();
    }
}
