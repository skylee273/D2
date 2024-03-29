package btcore.co.kr.d2band.view.couple;

import android.graphics.Bitmap;

public interface Couple {

    interface View{
        void showErrorMessage(String message);
        void showSaveView();
        void showImageBitmap(Bitmap bitmap, int type);
        void showNickName(String name, int type);
        void showCalendar(String date, String koreaDate, String goalDay, String dDay);
    }

    interface Presenter{
        void updateSaveView();
        void updateImage(Bitmap bitmap, int type);
        void updateNickName(String name, int type);
        void updateCalendar(String date);
    }
}
