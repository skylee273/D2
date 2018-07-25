package btcore.co.kr.d2band.view.couple.model;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.content.ContentValues.TAG;

public class CoupleModel {
    private String mDate = null;
    private long calDateDays;
    @SuppressLint("SimpleDateFormat")
    private
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-mm-dd");

    public boolean checkNickName(String name) {
        try {
            return name.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkDate(String date) {
        this.mDate = date;
        try {
            return mDate.length() > 0;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public boolean checkBitmap(Bitmap bitmap) {
        return bitmap != null;
    }

    public String getDate(){
        return String.valueOf(calDateDays);
    }
    public void setDate() {
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar todaCal = Calendar.getInstance(); //오늘날자 가져오기
            Calendar ddayCal = Calendar.getInstance(); //오늘날자를 가져와 변경시킴

            String date [];
            date = mDate.split("-");
            int year = Integer.valueOf(date[0]);
            int month = Integer.valueOf(date[1]);
            int day = Integer.valueOf(date[2]);

            month -= 1; // 받아온날자에서 -1을 해줘야함.
            ddayCal.set(year,month,day);// D-day의 날짜를 입력
            Log.e("테스트",simpleDateFormat.format(todaCal.getTime()) + "");
            Log.e("테스트",simpleDateFormat.format(ddayCal.getTime()) + "");

            long today = todaCal.getTimeInMillis()/86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
            long dday = ddayCal.getTimeInMillis()/86400000;
            long count = today - dday; // 오늘 날짜에서 dday 날짜를 빼주게 됩니다.
            Log.d(TAG,  "디데이 : " + String.valueOf(count));

            calDateDays = count;
        }
        catch (Exception e)
        {
            Log.d(TAG, e.toString());
        }
    }

    public String koreaDate() {
        long date = calDateDays;
        long year = 0;
        long month = 0;
        long day = 0;
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


}
