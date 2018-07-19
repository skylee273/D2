package btcore.co.kr.d2band.view.couple.presenter;

import android.graphics.Bitmap;

import btcore.co.kr.d2band.view.couple.Couple;
import btcore.co.kr.d2band.view.couple.model.CoupleModel;

public class CouplePresenter implements Couple.Presenter{

    Couple.View coupleView;
    CoupleModel coupleModel;

    public CouplePresenter(Couple.View view){
        this.coupleView = view;
        this.coupleModel = new CoupleModel();
    }

    @Override
    public void updateSaveView() {
        coupleView.showSaveView();
    }

    @Override
    public void updateImage(Bitmap bitmap, int type) {
        if(coupleModel.checkBitmap(bitmap)){
            coupleView.showImageBitmap(bitmap, type);
        }else{
            coupleView.showErrorMessage("이미지 업로드에 문제가 있습니다.");
        }
    }

    @Override
    public void updateNickName(String name, int type) {
        if(coupleModel.checkNickName(name)){
            coupleView.showNickName(name, type);
        }else{
            coupleView.showErrorMessage("닉네임을 다시 입력하세요.");
        }
    }

    @Override
    public void updateCalendar(String date) {
        if(coupleModel.checkDate(date)){
            coupleView.showCalendar(coupleModel.getDate(), coupleModel.koreaDate(), coupleModel.getMod(), coupleModel.getDay());
        }else{
            coupleView.showErrorMessage("날짜를 다시 선택해주세요.");
        }
    }

}
