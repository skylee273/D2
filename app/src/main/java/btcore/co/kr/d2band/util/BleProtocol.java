package btcore.co.kr.d2band.util;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BleProtocol {

    private byte D_START = 0x7F;
    private byte D_LENGHT = 0x01;
    private byte D_TYPE_NOTI = 0x0F;
    private byte D_TYPE_TIME = 0x0A;
    private byte D_CALL = 0x00;
    private byte D_CALL_START = 0x01;
    private byte D_CALL_STOP = 0x00;
    private byte D_MISS_CALL = 0x01;
    private byte D_MISS_COUNT;
    private byte D_SMS = 0x02;
    private byte D_SMS_COUNT;
    private byte D_KAKAO = 0x03;
    private byte D_KAKAO_COUNT;
    private byte D_SUB_CALL = 0x04;
    private byte D_SUB_MISS_CALL = 0x05;
    private byte D_SUB_SMS = 0x06;
    private byte D_SUB_KAKAO = 0x07;
    private byte D_REQUEST = 0x0B;
    private byte D_END = (byte) 0x00;
    private byte D_NAME_LENGTH;
    private byte [] D_NAME;
    private byte[] sendByte;
    private ArrayList<Byte> sendData = new ArrayList<>();



    private void clearData(){
        sendByte = null;
        sendData.clear();
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
        sendByte[18] = (byte) D_END;

        return sendByte;

    }

    public byte[] getCallStart(String name){
        clearData();
        sendByte = new byte[20];
        try {
            D_NAME = name.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_CALL);
        sendData.add(D_CALL_START);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getSubCallStart(String name){
        clearData();
        sendByte = new byte[20];
        try {
            D_NAME = name.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_SUB_CALL);
        sendData.add(D_CALL_START);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getCallEnd(String name){
        clearData();
        sendByte = new byte[20];
        try {
            D_NAME = name.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_CALL);
        sendData.add(D_CALL_STOP);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getSubCallEnd(String name){
        clearData();
        sendByte = new byte[20];
        try {
            D_NAME = name.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_SUB_CALL);
        sendData.add(D_CALL_STOP);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getMissedCall(String miss){
        clearData();
        sendByte = new byte[20];
        D_MISS_COUNT = (byte)Integer.parseInt(miss);
        D_LENGHT = 0x04;
        sendByte[0] = D_START;
        sendByte[1] = D_LENGHT;
        sendByte[2] = D_TYPE_NOTI;
        sendByte[3] = D_MISS_CALL;
        sendByte[4] = D_MISS_COUNT;
        sendByte[5] = D_END;
        return sendByte;
    }
    public byte[] getSubMissedCall(String miss){
        clearData();
        sendByte = new byte[20];
        D_MISS_COUNT = (byte)Integer.parseInt(miss);
        D_LENGHT = 0x04;
        sendByte[0] = D_START;
        sendByte[1] = D_LENGHT;
        sendByte[2] = D_TYPE_NOTI;
        sendByte[3] = D_SUB_MISS_CALL;
        sendByte[4] = D_MISS_COUNT;
        sendByte[5] = D_END;
        return sendByte;
    }
    public byte[] getSms(String s){
        clearData();
        sendByte = new byte[20];
        String [] sms = s.split("&&&&&");
        D_SMS_COUNT = (byte)Integer.parseInt(sms[1]);
        try {
            D_NAME = sms[0].getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_SMS);
        sendData.add(D_SMS_COUNT);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getSubSms(String s){
        clearData();
        sendByte = new byte[20];
        String [] sms = s.split("&&&&&");
        D_SMS_COUNT = (byte)Integer.parseInt(sms[1]);
        try {
            D_NAME = sms[0].getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_SUB_SMS);
        sendData.add(D_SMS_COUNT);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getKakao(String s){
        clearData();
        sendByte = new byte[20];
        String [] kakao = s.split("&&&&&");
        D_KAKAO_COUNT = (byte)Integer.parseInt(kakao[1]);
        try {
            D_NAME = kakao[0].getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_KAKAO);
        sendData.add(D_KAKAO_COUNT);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
        return sendByte;
    }
    public byte[] getSubKakao(String s){
        clearData();
        sendByte = new byte[20];
        String [] kakao = s.split("&&&&&");
        D_KAKAO_COUNT = (byte)Integer.parseInt(kakao[1]);
        try {
            D_NAME = kakao[0].getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        D_NAME_LENGTH = (byte)D_NAME.length;
        sendData.add(D_START);
        sendData.add(D_LENGHT);
        sendData.add(D_TYPE_NOTI);
        sendData.add(D_SUB_KAKAO);
        sendData.add(D_KAKAO_COUNT);
        sendData.add(D_NAME_LENGTH);
        for(byte NameByte : D_NAME){
            sendData.add(NameByte);
        }
        if(sendData.size() > 20) {
            D_LENGHT = 18;
            sendData.set(19, D_END);
            sendData.set(1, D_LENGHT);
        }else{
            sendData.add(D_END);
            sendData.set(1, (byte) (sendData.size() - 2));
        }

        for(int i = 0 ; i < sendData.size(); i++) {
            sendByte[i] = sendData.get(i);
        }
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
