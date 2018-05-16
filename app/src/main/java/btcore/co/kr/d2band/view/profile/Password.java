package btcore.co.kr.d2band.view.profile;

public interface Password {

    interface View{
        void showErrorMessage(String msg);
        void Update(String msg);
    }
    interface Presenter{
        void changePassword(String password);
    }

}
