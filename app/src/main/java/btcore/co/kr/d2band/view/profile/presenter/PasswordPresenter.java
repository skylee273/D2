package btcore.co.kr.d2band.view.profile.presenter;

import btcore.co.kr.d2band.view.profile.Password;
import btcore.co.kr.d2band.view.profile.Password.Presenter;
import btcore.co.kr.d2band.view.profile.model.PasswordModel;
import btcore.co.kr.d2band.view.profile.model.ProfileModel;

public class PasswordPresenter implements Presenter {

    Password.View passwordView;
    PasswordModel passwordModel;

    public PasswordPresenter(Password.View passwordView){
        this.passwordView = passwordView;
        this.passwordModel =  new PasswordModel();
    }

    public void onCreate() { passwordModel = new PasswordModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}


    @Override
    public void changePassword(String password) {
        if(passwordModel.checkPassword(password)){
            passwordModel.UpdatePassword(new PasswordModel.ApiListener() {
                @Override
                public void onSuccess(String message) {
                    passwordView.Update(message);
                }

                @Override
                public void onFail(String message) {
                    passwordView.showErrorMessage(message);
                }
            });
        }else{
            passwordView.showErrorMessage("비밀번호는 8자 이상입니다.");
        }
    }



}
