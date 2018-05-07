package btcore.co.kr.d2band.view.login.presenter;

import android.util.Log;

import btcore.co.kr.d2band.view.login.Login;
import btcore.co.kr.d2band.view.login.model.LoginModel;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class LoginPresenter implements Login.Presenter {

    Login.View loginView;
    LoginModel loginModel;

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

    @Override
    public void UserSet() {
        if(loginModel.checkUserId()){
            loginModel.callSetUser(new LoginModel.ApiListener() {
                @Override
                public void onSuccess(String message) {
                    Log.d("UserSet",message);
                }
                @Override
                public void onFail(String message) {
                    Log.d("UserSet",message);
                }
            });
        }else{
            Log.d("UserSet","저장 실패");

        }
    }


}
