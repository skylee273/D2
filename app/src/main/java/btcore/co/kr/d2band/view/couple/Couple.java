package btcore.co.kr.d2band.view.couple;

import android.graphics.Bitmap;

public interface Couple {
    interface view {
        void showErrorMessage(String message);
        void showImageBitmap(Bitmap bitmap, int type);
        void showNickName(String name, int type);
        void showCalendar(String date);
        void nextActivity();
    }

    interface Presenter{
        void updateImage(Bitmap bitmap, int type);
        void updateNickName(String name, int type);
        void updateCalendar(String date);
        void isNextActivity();
    }
}
