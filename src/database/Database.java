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
    private AppointmentRecord selectedAppointment;

    private int currentUserId;

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
            results = sqlStatement.executeQuery("SELECT userId, userName, password FROM user");

            while (results.next()) {
                dbUserName = results.getString("userName");
                dbPassword = results.getString("password");

                // If the entered userName and password matches an entry in the database, return success code.
                if (userName.matches(dbUserName) && password.matches(dbPassword)) {
                    currentUserId = results.getInt("userId");
                    return 2;
                }
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

    public void addCustomerRecord(String customerName, String address, String phoneNumber) {
        PreparedStatement statement;
        int addressId;

        try {
            statement = databaseConnection.prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, address);
            statement.setString(2,"");
            statement.setInt(3,1);
            statement.setString(4,"");
            statement.setString(5, phoneNumber);
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.systemDefault())));
            statement.setString(7, "");
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now(ZoneOffset.systemDefault())));
            statement.setString(9, "");

            statement.executeUpdate();

            results = sqlStatement.executeQuery("SELECT addressId FROM address WHERE address = '" + address + "'");
            results.next();
            addressId = Integer.parseInt(results.getString("addressId"));

            statement = databaseConnection.prepareStatement("INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, customerName);
            statement.setInt(2, addressId);
            statement.setInt(3, 1);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(5, "");
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, "");

            statement.executeUpdate();

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error adding customer to database");
        }
    }

    public void deleteCustomerRecord(Customer customer) {
        try {
            sqlStatement.executeUpdate("DELETE FROM customer WHERE customerId = " + customer.getCustomerId());
            sqlStatement.executeUpdate("DELETE FROM address WHERE addressId = " + customer.getAddressId());

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error deleting customer from database.");
        }
    }

    public void updateCustomerRecord(Customer customer) {
        PreparedStatement statement;

        try {
            statement = databaseConnection.prepareStatement("UPDATE address SET address = ?, address2 = ?, cityId = ?, postalCode = ?" +
                    ", phone = ?, createDate = ?, createdBy = ?, lastUpdate = ?, lastUpdateBy = ? WHERE addressId = ?");

            statement.setString(1, customer.getAddress());
            statement.setString(2, "");
            statement.setInt(3, 1);
            statement.setString(4, "");
            statement.setString(5, customer.getPhoneNumber());
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, "");
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(9, "");
            statement.setInt(10, customer.getAddressId());

            statement.executeUpdate();

            statement = databaseConnection.prepareStatement("UPDATE customer SET customerName = ?, addressId = ?, active = ?, createDate = ?, createdBy = ?" +
                    ", lastUpdate = ?, lastUpdateBy = ? WHERE customerId = ?");

            statement.setString(1, customer.getCustomerName());
            statement.setInt(2, customer.getAddressId());
            statement.setInt(3, 1);
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(5, "");
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, "");
            statement.setInt(8, customer.getCustomerId());

            statement.executeUpdate();

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error updating customer in database.");
        }
    }

    public void addAppointmentRecord(int customerId, int userId, String type, Timestamp start, Timestamp end) {
        PreparedStatement statement;

        try {
            statement = databaseConnection.prepareStatement("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, " +
                    "createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setInt(1, customerId);
            statement.setInt(2, userId);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, type);
            statement.setString(8, "");
            statement.setTimestamp(9, start);
            statement.setTimestamp(10, end);
            statement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(12, "");
            statement.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(14, "");

            statement.executeUpdate();

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error adding appointment to database.");
        }
    }

    public void deleteAppointmentRecord(AppointmentRecord appointment) {
        try {
            sqlStatement.executeUpdate("DELETE FROM appointment WHERE appointmentId = " + appointment.getAppointmentId());

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error deleting appointment from database.");
        }
    }

    public void updateAppointmentRecord(AppointmentRecord appointment) {
        PreparedStatement statement;

        try {
            statement = databaseConnection.prepareStatement("UPDATE appointment SET type = ?, start = ?, end = ? WHERE appointmentId = ?");

            statement.setString(1, appointment.getType());
            statement.setTimestamp(2, appointment.getStart());
            statement.setTimestamp(3, appointment.getEnd());
            statement.setInt(4, appointment.getAppointmentId());

            statement.executeUpdate();

            constructDatabaseRecords();
        }
        catch (Exception e) {
            utility.displayError("Error updating appointment in database.");
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

    public AppointmentRecord getSelectedAppointment() { return selectedAppointment; }

    public void setSelectedAppointment(AppointmentRecord selectedAppointment) { this.selectedAppointment = selectedAppointment; }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}