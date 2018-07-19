package btcore.co.kr.d2band.view.couple.model;

import android.graphics.Bitmap;

public class CoupleSettingModel {
    String mName;
    String mDate;
    Bitmap mBitmap = null;

    public boolean checkNickName(String name) {
        this.mName = name;
        try {
            if (mName.length() > 0) {
                return true;
            }else{
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkDate(String date){
        this.mDate = date;
        try{
            if(mDate.length() > 0) {
                return true;
            }else{
                return false;
            }
        }catch (NullPointerException e){
            return false;
        }
    }

    public boolean checkBitmap(Bitmap bitmap){
        this.mBitmap = bitmap;
        if(mBitmap != null){
            return true;
        }else{
            return false;
        }
    }


}
