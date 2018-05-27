package btcore.co.kr.d2band.view.message;

public interface Message {

    interface View{
        void showErrorMessage(String msg);
        void MessageUpdate(String msg);
        void updateListView(String []Receiver);
    }
    interface Presenter{
        void changeMessage(String msg);
        void saveMsg(String msg);
        void plusReceiver(String name, String phone);
        void selectReceiver();
    }


}
