package database;

public class AppointmentsPerCustomer {
    private String customerName;
    private int amount;

    public AppointmentsPerCustomer(String customerName) {
        this.customerName = customerName;
        this.amount = 0;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
