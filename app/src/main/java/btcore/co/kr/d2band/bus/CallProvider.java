package btcore.co.kr.d2band.bus;

public class CallProvider {
    private static final CustomBus BUS = new CustomBus();

    public static CustomBus getInstance() {
        return BUS;
    }

    private CallProvider() {
    }

}
