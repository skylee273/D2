package btcore.co.kr.d2band.database;

/**
 * Created by leehaneul on 2018-01-22.
 */

public class mySql {
    private static String URL = "http://103.60.125.37:8080/";

    private static String DIOTALK = "http://211.110.139.145/";

    public static String URL_INSERT_RECV = URL + "DioBand/Message/ReceiveInsert.jsp";

    static String URL_GET_RECEIVER = DIOTALK + "diotalk/getReceiver.php";

    static String URL_GET_STEP = DIOTALK + "diotalk/getStep.php";

    public static String URL_GET_ID = DIOTALK + "diotalk/getID.php";

    public static String URL_GET_PW = DIOTALK + "diotalk/getPassword.php";

    public static String URL_GET_HEART = DIOTALK + "diotalk/getHeart.php";

    public static String URL_CALL_LOGIN = DIOTALK + "diotalk/callLogin.php";

    public static String URL_CALL_REGISTER = DIOTALK + "diotalk/callRegister.php";

    public static String URL_DELETE_RECEIVER = DIOTALK + "diotalk/deleteReceiver.php";

    static String URL_SET_STEP = DIOTALK + "diotalk/setStep.php";

    static String URL_SET_HEART = DIOTALK + "diotalk/setHeart.php";



}
