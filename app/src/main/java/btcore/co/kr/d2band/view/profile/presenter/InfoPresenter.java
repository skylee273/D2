package btcore.co.kr.d2band.view.profile.presenter;

import btcore.co.kr.d2band.view.profile.Info;
import btcore.co.kr.d2band.view.profile.model.InfoModel;

public class InfoPresenter implements Info.Presenter{
    private Info.View infoView;
    private InfoModel infoModel;

    public InfoPresenter(Info.View infoView){
        this.infoView = infoView;
        this.infoModel = new InfoModel();
    }


    public void onCreate() { infoModel = new InfoModel(); }
    public void onPause() {}
    public void onResume() {}
    public void onDestroy() {}

    @Override
    public void nextActivity() {

    }

    @Override
    public void setUser(String name, String birthday, String gender, String height, String weight, String phone, String addr) {
        if(infoModel.checkUser(name, birthday, gender, height, weight, phone, addr)){
            if(gender.contains("m")){ gender = "남성";}
            else {gender = "여성";}
            infoView.showUserInfo(name, birthday, gender, height, weight, phone, addr);
        }else{
            infoView.showErrorMessage("유저 정보가 업로드 되지 않았습니다.");
        }
    }


}
