package btcore.co.kr.d2band.bus;

public class CallBusEvent {
    private String eventData;
    private int callType;
    public CallBusEvent(int callType, String eventData) {
        this.callType = callType;
        this.eventData = eventData;
    }

    public String getEventData() {
        return eventData;
    }
    public int getCallType() { return  callType; }

}
