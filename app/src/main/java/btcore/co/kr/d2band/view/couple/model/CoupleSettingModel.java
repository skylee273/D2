package btcore.co.kr.d2band.view.couple.model;

import android.graphics.Bitmap;

public class CoupleSettingModel {

    public boolean checkNickName(String name) {
        try {
            return name.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkDate(String date){
        try{
            return date.length() > 0;
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean checkBitmap(Bitmap bitmap){
        return bitmap != null;
    }


}
