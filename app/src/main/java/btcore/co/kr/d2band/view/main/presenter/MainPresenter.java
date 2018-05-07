package btcore.co.kr.d2band.view.main.presenter;

import btcore.co.kr.d2band.view.login.model.LoginModel;
import btcore.co.kr.d2band.view.main.Main;
import btcore.co.kr.d2band.view.main.model.MainModel;

/**
 * Created by leehaneul on 2018-02-20.
 */

public class MainPresenter implements Main.Presenter {

    Main.View mainView;
    MainModel mainModel;

    public MainPresenter(Main.View mainView){
        this.mainView = mainView;
        this.mainModel = new MainModel();
    }
    public void onCreate() { mainModel = new MainModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}


    @Override
    public void checkBluetooth() {
        if(!mainModel.checkBleState()){
            mainView.showErrorMessage("블루투스를 이용할 수 없는 상태입니다.");
        }
    }

    @Override
    public void isEnabled() {
        if(!mainModel.isEnable()){
            mainView.startRequestEnableBt();
        }else {
            mainView.startRequsetSelectDevice();
        }
    }

    @Override
    public void isNext() {
        mainView.startMainActivity();
    }

}
