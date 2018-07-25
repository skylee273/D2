package btcore.co.kr.d2band.view.find.presenter;

import btcore.co.kr.d2band.view.find.model.FindModel;

/**
 * Created by leehaneul on 2018-02-12.
 */

public class FindPresenter implements Find.Presenter {

    private Find.View findView;
    private FindModel findModel;

    public FindPresenter(Find.View findView) {
        this.findView = findView;
        this.findModel = new FindModel();
    }

    @Override
    public void initFindId(String name, String phone) {
        findModel.setIdInfo(name, phone);
    }

    @Override
    public void initFindPw(String id, String name, String phone) {
        findModel.setPwInfo(id, name, phone);
    }

    @Override
    public void callIdDialog() {
        if (findModel.checkInfo()) {
            findModel.getInfo(new FindModel.FindApiListner() {
                @Override
                public void onSuccess(String message) {
                    findView.startDialog("아이디 찾기", message);
                }

                @Override
                public void onFail(String message) {
                    findView.showErrorMessage(message);
                }
            });

        } else {
            findView.showErrorMessage("정보가 입력되지 않았습니다.");
        }
    }

    @Override
    public void callPwDialog() {
        if (findModel.checkPwInfo()) {
            findModel.getPwInfo(new FindModel.FindPwApiListner() {
                @Override
                public void onSuccess(String msg, String pw) {
                    findView.startPwDialog(msg, pw);
                }

                @Override
                public void onFail(String message) {
                    findView.showErrorMessage(message);
                }
            });
        } else {
            findView.showErrorMessage("정보가 입력되지 않았습니다.");
        }
    }

}
