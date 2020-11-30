package com.thinqtv.thinqtv_android.data.model;

public class RSVP {
    private String startTime;
    private int eventId;
    private String name;

    public RSVP(String startTime, int eventId, String name) {
        this.startTime = startTime;
        this.eventId = eventId;
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }
    public int getEventId() {
        return eventId;
    }
    public String getName() {
        return name;
    }
}