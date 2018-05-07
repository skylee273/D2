package btcore.co.kr.d2band.view.profile;

public interface Profile {

    interface View{
        void showErrorMessage(String msg);
        void startMainActivity();
        void showUserInfo(String name, String id);
    }

    interface Presenter{
       void nextActivity();
       void getUser();
    }


}
