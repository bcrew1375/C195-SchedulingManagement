package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Customer {
    private int customerId;
    private int addressId;

    private String address;
    private String customerName;
    private String phoneNumber;

    private Database database;

    private ObservableList<AppointmentRecord> appointmentList;

    public Customer(CustomerRecord customerRecord) {
        this.customerId = customerRecord.getCustomerId();
        this.customerName = customerRecord.getCustomerName();
        this.addressId = customerRecord.getAddressId();

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ObservableList<AppointmentRecord> getAppointmentList() {
        return appointmentList;
    }
}
