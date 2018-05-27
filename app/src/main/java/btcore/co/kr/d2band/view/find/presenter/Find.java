package btcore.co.kr.d2band.view.find.presenter;

/**
 * Created by leehaneul on 2018-02-12.
 */

public interface Find {
    interface  View{
        void showErrorMessage(String message);
        void startDialog(String title, String context);
        void startPwDialog(String msg, String pw);

    }

    interface  Presenter{
        void initFindId(String name, String phone);
        void initFindPw(String name, String phone);
        void callIdDialog();
        void callPwDialog();
    }
}
