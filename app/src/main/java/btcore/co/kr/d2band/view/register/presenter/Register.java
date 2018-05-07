package btcore.co.kr.d2band.view.register.presenter;

/**
 * Created by leehaneul on 2018-01-22.
 */

public interface Register {
    interface  View{
        void showErrorMessage(String message);
        void startMainActivity();
    }

    interface  Presenter{
        void initUserData(String id, String pw, String pwConfirm, String name, String birthday, String gender, String height, String weight, String phone, String address);
        void callSignup();
    }
}
