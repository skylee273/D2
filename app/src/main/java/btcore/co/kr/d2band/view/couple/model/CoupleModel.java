package btcore.co.kr.d2band.view.couple.model;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CoupleModel {
    String mName;
    String mDate = null;
    Bitmap mBitmap = null;
    long calDateDays;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-mm-dd");

    public boolean checkNickName(String name) {
        this.mName = name;
        try {
            if (mName.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkDate(String date) {
        this.mDate = date;
        try {
            if (mDate.length() > 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        if (mBitmap != null) {
            return true;
        } else {
            return false;
        }
    }

    public String getDate() {
        try {

            SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
            // date1, date2 두 날짜를 parse()를 통해 Date형으로 변환.
            Date FirstDate = format.parse(mDate);
            Date SecondDate = format.parse(getTime());

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            calDateDays = calDate / (24 * 60 * 60 * 1000);

            calDateDays = Math.abs(calDateDays);


        } catch (ParseException e) {
            // 예외 처리
        }
        return String.valueOf(calDateDays);
    }

    public String koreaDate() {
        int date = Integer.parseInt(getDate());
        int year = 0;
        int month = 0;
        int day = 0;
        if (date >= 365) {
            year = date / 365;
           date =  date - ( 365 * year);
        }
        if (date >= 30){
            month = date / 30;
            date = date - (30 * month);
        }
        day = date;

        return String.valueOf(year) + "년 " + String.valueOf(month) + "개월 " + String.valueOf(day) + "일";
    }

    public String getMod(){
        long mod = calDateDays / 100;
        long goalDate = (mod + 1) * 100;
        return String.valueOf(goalDate);
    }

    public String getDay(){
        long mod = calDateDays / 100;
        long goalDate = (mod + 1) * 100;
        long dDay = goalDate - calDateDays;
        return String.valueOf(dDay);
    }
    private String getTime() {
        long mNow;
        Date mDate;
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }


}
