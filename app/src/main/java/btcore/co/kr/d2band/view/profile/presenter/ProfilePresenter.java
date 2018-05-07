package btcore.co.kr.d2band.view.profile.presenter;

import btcore.co.kr.d2band.view.login.model.LoginModel;
import btcore.co.kr.d2band.view.profile.Profile;
import btcore.co.kr.d2band.view.profile.model.ProfileModel;

public class ProfilePresenter  implements Profile.Presenter{
    Profile.View profileView;
    ProfileModel profileModel;


    public ProfilePresenter(Profile.View profileView){
        this.profileView = profileView;
        this.profileModel = new ProfileModel();
    }

    public void onCreate() { profileModel = new ProfileModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}

    @Override
    public void nextActivity() {
        profileView.startMainActivity();
    }

    @Override
    public void getUser() {
      if(profileModel.checkUser()){
          profileView.showUserInfo(profileModel.getName(), profileModel.getEmail());
      }else{
          profileView.showErrorMessage("유저 정보가 확인되지 않았습니다.");
      }
    }
}
