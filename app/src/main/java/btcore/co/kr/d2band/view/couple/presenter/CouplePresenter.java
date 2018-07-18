package btcore.co.kr.d2band.view.couple.presenter;

import android.graphics.Bitmap;

import btcore.co.kr.d2band.view.couple.Couple;
import btcore.co.kr.d2band.view.couple.model.CoupleModel;

public class CouplePresenter implements Couple.Presenter {

    Couple.view coupleView;
    CoupleModel coupleModel;

    public CouplePresenter(Couple.view coupleView){
        this.coupleView = coupleView;
        this.coupleModel = new CoupleModel();
    }

    @Override
    public void updateImage(Bitmap bitmap, int type) {

    }

    @Override
    public void updateNickName(String name, int type) {

    }

    @Override
    public void updateCalendar(String date) {

    }

    @Override
    public void isNextActivity() {

    }
}
