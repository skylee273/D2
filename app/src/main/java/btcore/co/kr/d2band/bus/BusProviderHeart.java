package btcore.co.kr.d2band.bus;

public class BusProviderHeart {

    private static final CustomBus BUS = new CustomBus();

    public static CustomBus getInstance() {
        return BUS;
    }

    private BusProviderHeart() {
    }

}
