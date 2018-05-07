package btcore.co.kr.d2band.view.register.presenter;

import btcore.co.kr.d2band.view.register.model.RegisterModel;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class RegisterPresenter implements Register.Presenter {

    Register.View registerView;
    RegisterModel registerModel;

    public RegisterPresenter(Register.View registerView){
        this.registerView = registerView;
        this.registerModel = new RegisterModel();
    }

    public void onCreate() { registerModel = new RegisterModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}

    @Override
    public void initUserData(String id, String pw, String pwConfirm, String name, String birthday, String gender ,String height, String weight, String phone, String address) {

        registerModel.setUserData(id, pw, pwConfirm, name, birthday, gender ,height, weight, phone, address);
        if(registerModel.checkPw()){
            registerModel.setSalt();
        }else{
            registerView.showErrorMessage("회원가입 정보가 잘못 되었습니다.");
        }

    }

    @Override
    public void callSignup() {
        String registerMessage = registerModel.checkUserDataLength();
        if(registerMessage.equals("성공")){
            registerModel.callSignup(new RegisterModel.ApiListener() {
                @Override
                public void onSuccess(String message) {
                    registerView.startMainActivity();
                }

                @Override
                public void onFail(String message) {
                    registerView.showErrorMessage(message);
                }
            });
        }else{
            registerView.showErrorMessage(registerMessage);
        }
    }


}
