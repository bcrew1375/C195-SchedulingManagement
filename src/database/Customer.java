package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Timestamp;

public class Customer {
    private int customerId;
    private String customerName;
    private int addressId;
    private String address;
    private String phoneNumber;
    private int appointmentId;
    private String type;
    private String url;
    private Timestamp start;
    private Timestamp end;

    private ObservableList<AppointmentRecord> appointmentList;

    Database database;

    public Customer(CustomerRecord customerRecord/*int customerId, String customerName, int addressId, String address, String phoneNumber, int appointmentId, String type,
                    String url, LocalDateTime start, LocalDateTime end*/) {
        this.customerId = customerRecord.getCustomerId();
        this.customerName = customerRecord.getCustomerName();
        this.addressId = customerRecord.getAddressId();
        //this.address = customerRecord.;
        //this.phoneNumber = phoneNumber;

        database = Database.getInstance();

        for (int i = 0; i < database.getAddressRecords().size(); i++) {
            if (database.getAddressRecords().get(i).getAddressId() == customerRecord.getAddressId()) {
                this.address = database.getAddressRecords().get(i).getAddress();
                this.phoneNumber = database.getAddressRecords().get(i).getPhone();
            }
        }

        appointmentList = FXCollections.observableArrayList();

        for (int i = 0; i < database.getAppointmentRecords().size(); i++) {
            if (database.getAppointmentRecords().get(i).getCustomerId() == customerRecord.getCustomerId()) {
                appointmentList.add(database.getAppointmentRecords().get(i));
            }
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ObservableList getAppointmentList() {
        return appointmentList;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }
}
