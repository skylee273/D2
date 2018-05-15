package btcore.co.kr.d2band.view.profile;

public interface Info {

    interface View{
        void showErrorMessage(String msg);
        void startMainActivity();
        void showUserInfo(String name, String birthday, String gender, String height, String weight, String phone, String addr);
    }

    interface Presenter{
        void nextActivity();
        void setUser(String name, String birthday, String gender, String height, String weight, String phone, String addr);
    }


}
