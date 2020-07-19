package database;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;

import database.CustomerRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utility.Utility;

public class Database {

    private ObservableList<AddressRecord> addressRecords;
    private ObservableList<AppointmentRecord> appointmentRecords;
    private ObservableList<CustomerRecord> customerRecords;
    private ObservableList<Customer> combinedCustomerRecords;

    private Connection databaseConnection;
    private ResultSet results;
    private Statement sqlStatement;

    private Utility utility = new Utility();

    private Customer selectedCustomer;

    private Database() {

        try {
            Class.forName("com.mysql.jdbc.Driver");

            databaseConnection = DriverManager.getConnection("jdbc:mysql://3.227.166.251:3306/U07b4Z", "U07b4Z", "53688979772");
            sqlStatement = databaseConnection.createStatement();
        }
        catch (Exception e) {
            utility.displayError("There was an error connecting to the database.");
            javafx.application.Platform.exit();
        }
    }

    public static Database getInstance() {
        return DatabaseHolder.INSTANCE;
    }

    private static class DatabaseHolder {
        private static final Database INSTANCE = new Database();
    }

    public int checkLoginCredentials(String userName, String password) {

        String dbUserName, dbPassword;

        try {
            results = sqlStatement.executeQuery("SELECT userName, password FROM user");

            while (results.next()) {
                dbUserName = results.getString("userName");
                dbPassword = results.getString("password");

                // If the entered userName and password matches an entry in the database, return success code.
                if (userName.matches(dbUserName) && password.matches(dbPassword))
                    return 2;
            }
        }
        catch (Exception e) {
            return 1;
        }

        return 0;
    }

    // Create local objects for database records.
    public void constructDatabaseRecords() {
        addressRecords = FXCollections.observableArrayList();
        appointmentRecords = FXCollections.observableArrayList();
        customerRecords = FXCollections.observableArrayList();
        combinedCustomerRecords = FXCollections.observableArrayList();

        addressRecords.removeAll();
        appointmentRecords.removeAll();
        customerRecords.removeAll();
        combinedCustomerRecords.removeAll();

        try {
            results = sqlStatement.executeQuery("SELECT * FROM customer");

            while (results.next()) {
                customerRecords.add(new CustomerRecord(Integer.parseInt(results.getString("customerId")),
                        results.getString("customerName"), Integer.parseInt(results.getString("addressId"))));
            }

            results = sqlStatement.executeQuery("SELECT * FROM address");

            while (results.next()) {
                addressRecords.add(new AddressRecord(Integer.parseInt(results.getString("addressId")),
                        results.getString("address"), results.getString("phone")));
            }

            results = sqlStatement.executeQuery("SELECT * FROM appointment");

            while (results.next()) {
                appointmentRecords.add(new AppointmentRecord(Integer.parseInt(results.getString("appointmentId")),
                        Integer.parseInt(results.getString("customerId")), Integer.parseInt(results.getString("userId")),
                        results.getString("type"), results.getString("url"),
                        LocalDateTime.parse(results.getString("start"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.of("GMT+0")),
                        LocalDateTime.parse(results.getString("end"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).atZone(ZoneId.of("GMT+0"))));
            }

            for (int i = 0; i < customerRecords.size(); i++) {
                combinedCustomerRecords.add(new Customer(customerRecords.get(i)));
            }
        }
        catch (Exception e) {
            utility.displayError("Error creating local database objects.");
        }
    }

    public ObservableList<AddressRecord> getAddressRecords() {
        return addressRecords;
    }

    public ObservableList<AppointmentRecord> getAppointmentRecords() {
        return appointmentRecords;
    }

    public ObservableList<Customer> getCombinedCustomerList() {
        return combinedCustomerRecords;
    }

    public ObservableList<CustomerRecord> getCustomerRecords() {
        return customerRecords;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
    }

    public Customer getSelectedCustomer() {
        return this.selectedCustomer;
    }

    public void addCustomerRecord(String customerName, String address, String phoneNumber) {
        String statement;
        int addressId;

        try {
            statement = ("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    " VALUES ('" + address + "', '', 1, '', '" + phoneNumber + "', '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) +
                    "', '', '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) + "', '')");

            sqlStatement.executeUpdate(statement);

            results = sqlStatement.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "'");
            results.next();
            addressId = Integer.parseInt(results.getString("addressId"));

            statement = ("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    "VALUES ('" + customerName + "', " + addressId + ", 1, '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) +
                    "', '', '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) + "', '')");

            sqlStatement.executeUpdate(statement);
        }
        catch (Exception e) {
            utility.displayError("Error adding customer to database");
        }
    }

    public void deleteCustomerRecord(Customer customer) {
        String statement;
        int addressId;

        try {
            sqlStatement.executeUpdate("DELETE FROM customer WHERE customerId = " + customer.getCustomerId());
            sqlStatement.executeUpdate("DELETE FROM address WHERE addressId = " + customer.getAddressId());
        }
        catch (Exception e) {
            utility.displayError("Error deleting customer from database.");
        }
    }

    public void updateCustomerRecord(Customer customer) {
        String statement;
        int addressId;

        try {
            statement = ("UPDATE address SET address = '" + customer.getAddress() + "', address2 = '', cityId = 1, postalCode = '', phone = '" +
                    customer.getPhoneNumber() + "', createDate = '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) +
                    "', createdBy = '', lastUpdate = '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) + "', lastUpdateBy = ''" +
                    "WHERE addressId = " + customer.getAddressId());

            sqlStatement.executeUpdate(statement);

            results = sqlStatement.executeQuery("SELECT addressId FROM address WHERE address = '" + customer.getAddress() + "'");
            results.next();
            addressId = Integer.parseInt(results.getString("addressId"));

            statement = ("UPDATE customer SET customerName = '" + customer.getCustomerName() + "', addressId = " + addressId + ", active = 1" +
                    ", createDate = '" + Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) + "', createdBy = '', lastUpdate = '" +
                    Timestamp.valueOf(LocalDateTime.now(ZoneOffset.UTC)) + "', lastUpdateBy = '' WHERE customerId = " + customer.getCustomerId());

            sqlStatement.executeUpdate(statement);
        }
        catch (Exception e) {
            utility.displayError("Error updating customer from database.");
        }
    }
}