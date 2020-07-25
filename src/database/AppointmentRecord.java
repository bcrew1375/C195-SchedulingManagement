package database;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class AppointmentRecord {
    private int appointmentId;
    private int customerId;
    private int userId;

    private String type;
    private String url;

    private Timestamp start;
    private Timestamp end;

    private String displayStartTime;
    private String displayEndTime;

    public AppointmentRecord(int appointmentId, int customerId, int userId, String type, String url, Timestamp start, Timestamp end) {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.userId = userId;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;

        this.displayStartTime = new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(this.start);
        this.displayEndTime = new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(this.end);
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
        this.displayStartTime = new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(this.start);
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
        this.displayEndTime = new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(this.end);
    }

    public String getDisplayStartTime() {
        return displayStartTime;
    }

    public void setDisplayStartTime(String displayStartTime) {
        this.displayStartTime = displayStartTime;
    }

    public String getDisplayEndTime() {
        return displayEndTime;
    }

    public void setDisplayEndTime(String displayEndTime) {
        this.displayEndTime = displayEndTime;
    }
}