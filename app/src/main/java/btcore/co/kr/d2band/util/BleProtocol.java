package btcore.co.kr.d2band.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BleProtocol {

    private byte D_START = 0x7F;
    private byte D_LENGHT;
    private byte D_TYPE_NOTI = 0x0F;
    private byte D_TYPE_TIME = 0x0A;
    private byte D_CALL = 0x00;
    private byte D_MISS_CALL = 0x01;
    private byte D_SMS = 0x02;
    private byte D_KAKAO = 0x03;
    private byte D_SUB_CALL = 0x04;
    private byte D_SUB_SMS = 0x05;
    private byte D_SUB_KAKAO = 0x07;
    private byte D_REQUEST = 0x0B;
    private byte D_END = (byte) 0xff;
    private byte[] sendByte;
    private String byteToStr;


    private void clearData(){
        sendByte = null;
        byteToStr = null;
    }

    public byte[] Requset() {
        clearData();
        D_LENGHT = 0x03;
        sendByte = new byte[20];
        sendByte[0] = D_START;
        sendByte[1] = D_LENGHT;
        sendByte[2] = D_REQUEST;
        sendByte[3] = 0x01;
        sendByte[4] = D_END;
        return sendByte;
    }

    public byte[] getTimeInfo(String timeData, int WeekData) {
        clearData();
        D_LENGHT = 0x11;
        sendByte = new byte[20];
        sendByte[0] = D_START;      //Start ID
        sendByte[1] = D_LENGHT;      //Length
        sendByte[2] = D_TYPE_TIME;      //Type
        sendByte[3] = (byte) timeData.charAt(0);      //2        //year
        sendByte[4] = (byte) timeData.charAt(1);      //0
        sendByte[5] = (byte) timeData.charAt(2);         //1
        sendByte[6] = (byte) timeData.charAt(3);         //8
        sendByte[7] = (byte) timeData.charAt(4);         //0         //month
        sendByte[8] = (byte) timeData.charAt(5);         //2
        sendByte[9] = (byte) timeData.charAt(6);         //0         //date
        sendByte[10] = (byte) timeData.charAt(7);         //5
        sendByte[11] = (byte) timeData.charAt(8);         //0        //hour
        sendByte[12] = (byte) timeData.charAt(9);         //8
        sendByte[13] = (byte) timeData.charAt(10);         //2       //minute
        sendByte[14] = (byte) timeData.charAt(11);         //2
        sendByte[15] = (byte) timeData.charAt(12);         //1           /second
        sendByte[16] = (byte) timeData.charAt(13);         //1
        sendByte[17] = (byte) WeekData;         //1
        sendByte[18] = (byte) 0xff;

        return sendByte;

    }


    public int getWeek() {
        Calendar cal = Calendar.getInstance();
        int week = cal.get(cal.DAY_OF_WEEK);
        return (week - 1);
    }

    public String getDate() {
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA);
        Date currentTime = new Date();
        String mTime = mSimpleDateFormat.format(currentTime);

        return mTime;
    }


}
