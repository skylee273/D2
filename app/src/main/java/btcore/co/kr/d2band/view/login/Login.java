package btcore.co.kr.d2band.view.login;

import java.util.ArrayList;

/**
 * Created by leehaneul on 2018-01-22.
 */

public interface Login {

    interface View{
        void showErrorMessage(String msg);
        void startMainActivity();
    }

    interface Presenter{
        void initUserData(String id, String pw);
        void callLogin();
        void UserSet();
        void RecvSet();
    }
}
