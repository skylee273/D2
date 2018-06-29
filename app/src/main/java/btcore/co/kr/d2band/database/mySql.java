package btcore.co.kr.d2band.database;

import btcore.co.kr.d2band.util.ParserUtils;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class mySql {
    private static String URL = "http://103.60.125.37:8080/";

    public static String URL_REGISTER = URL + "DioBand/User/Register.jsp";

    public static String URL_LOGIN = URL + "DioBand/User/Login.jsp";

    public static String URL_FIND_ID = URL + "DioBand/User/IdSelect.jsp";

    public static String URL_FIND_PW = URL + "DioBand/User/PwSelect.jsp";

    public static String URL_SET_USER = URL + "DioBand/User/UserSelect.jsp";

    public static String URL_UPDATE_PASSWORD = URL + "DioBand/User/PasswordUpdate.jsp";

    public static String URL_INSERT_RECV = URL + "DioBand/Message/ReceiveInsert.jsp";

    public static String URL_DELETE_RECV = URL + "DioBand/Message/ReceiveDelete.jsp";

    public static String URL_SET_RECEIVE = URL + "DioBand/Message/ReceiveSelect.jsp";

    public static String URL_INSERT_STEP = URL + "DioBand/Step/StepInsert.jsp";

    public static String URL_INSERT_HEART = URL + "DioBand/Heart/HeartInsert.jsp";

    public static String URL_SELECT_HEART = URL + "DioBand/Heart/HeartSelect.jsp";
}
