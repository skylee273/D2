package btcore.co.kr.d2band.view.main.presenter;

import btcore.co.kr.d2band.view.main.Bottom;

/**
 * Created by leehaneul on 2018-02-26.
 */

public class BottomPresenter implements Bottom.Presenter {

    Bottom.View bottomView;

    public BottomPresenter(Bottom.View bottomView){
        this.bottomView = bottomView;
    }


    @Override
    public void chekCurrentPage(int page) {
        bottomView.showImage(page);

    }

    @Override
    public void startActivity(int page) {
        bottomView.startMainActivity(page);
    }
}
