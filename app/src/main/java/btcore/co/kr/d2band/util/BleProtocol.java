package btcore.co.kr.d2band.util;

public class BleProtocol {

    private byte D_START = 0x7F;
    private byte D_LENGHT;
    private byte D_TYPE_NOTI = 0x0F;
    private byte D_TYPE_TIME;
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


}
