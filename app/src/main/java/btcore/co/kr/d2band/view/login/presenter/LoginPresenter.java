package btcore.co.kr.d2band.view.login.presenter;

import btcore.co.kr.d2band.view.login.Login;
import btcore.co.kr.d2band.view.login.model.LoginModel;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class LoginPresenter implements Login.Presenter {

    private Login.View loginView;
    private LoginModel loginModel;

    public LoginPresenter(Login.View loginView){
        this.loginView = loginView;
        this.loginModel = new LoginModel();
    }


    public void onCreate() { loginModel = new LoginModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}


    @Override
    public void initUserData(String id, String pw) {
        loginModel.setUserData(id, pw);
    }

    @Override
    public void callLogin() {

        if(loginModel.checkUserData()){
            loginModel.callLogin(new LoginModel.ApiListener() {
                @Override
                public void onSuccess(String message) {
                    loginView.startMainActivity();
                }

                @Override
                public void onFail(String message) {
                    loginView.showErrorMessage(message);
                }
            });
        }else{
            loginView.showErrorMessage("아이디 또는 비밀번호를 입력하세요");
        }
    }


}
