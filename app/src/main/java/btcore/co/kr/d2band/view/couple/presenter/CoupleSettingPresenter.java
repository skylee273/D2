package btcore.co.kr.d2band.view.couple.presenter;

import android.graphics.Bitmap;

import btcore.co.kr.d2band.view.couple.CoupleSetting;
import btcore.co.kr.d2band.view.couple.model.CoupleSettingModel;

public class CoupleSettingPresenter implements CoupleSetting.Presenter {

    private CoupleSetting.view coupleView;
    private CoupleSettingModel coupleSettingModel;

    public CoupleSettingPresenter(CoupleSetting.view coupleView){
        this.coupleView = coupleView;
        this.coupleSettingModel = new CoupleSettingModel();
    }

    @Override
    public void updateImage(Bitmap bitmap, int type) {
        if(coupleSettingModel.checkBitmap(bitmap)){
            coupleView.showImageBitmap(bitmap, type);
        }else{
            coupleView.showErrorMessage("이미지 업로드에 문제가 있습니다.");
        }
    }

    @Override
    public void updateNickName(String name, int type) {
        if(coupleSettingModel.checkNickName(name)){
            coupleView.showNickName(name, type);
        }else{
            coupleView.showErrorMessage("닉네임을 다시 입력하세요.");
        }
    }

    @Override
    public void updateCalendar(String date) {
        if(coupleSettingModel.checkDate(date)){
            coupleView.showCalendar(date);
        }else{
            coupleView.showErrorMessage("날짜를 다시 선택해주세요.");
        }
    }

    @Override
    public void isNextActivity() {
        coupleView.nextActivity();
    }

    @Override
    public void updateSaveView() {
        coupleView.showSaveView();
    }
}
