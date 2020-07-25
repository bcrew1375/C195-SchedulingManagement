package database;

public class AddressRecord {

    private int addressId;

    private String address;
    private String phone;

    public AddressRecord(int addressId, String address, String phone) {
        this.addressId = addressId;
        this.address = address;
        this.phone = phone;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
