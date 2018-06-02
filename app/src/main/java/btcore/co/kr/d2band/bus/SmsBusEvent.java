package btcore.co.kr.d2band.bus;

public class SmsBusEvent {
    private String eventData;

    public SmsBusEvent(String eventData) {
        this.eventData = eventData;
    }

    public String getEventData() {
        return eventData;
    }

}
