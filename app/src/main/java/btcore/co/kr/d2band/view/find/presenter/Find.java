package btcore.co.kr.d2band.view.find.presenter;

/**
 * Created by leehaneul on 2018-02-12.
 */

public interface Find {
    interface  View{
        void showErrorMessage(String message);
        void startDialog(String title, String context);
        void startEmailDialog(String title, String email, String pw);

    }

    interface  Presenter{
        void initFindId(String name, String phone);
        void initFindPw(String name, String phone, String email);
        void callIdDialog();
        void callPwDialog();
    }
}
