package database;

import java.sql.*;
import java.time.*;

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

    private Utility utility;

    private AppointmentRecord selectedAppointment;
    private Customer selectedCustomer;

    private int currentUserId;

    private Database() {

        addressRecords = FXCollections.observableArrayList();
        appointmentRecords = FXCollections.observableArrayList();
        customerRecords = FXCollections.observableArrayList();
        combinedCustomerRecords = FXCollections.observableArrayList();

        utility = new Utility();

        try {
            Class.forName("com.mysql.jdbc.Driver");

            databaseConnection = DriverManager.getConnection("jdbc:mysql://3.227.166.251:3306/U07b4Z", "U07b4Z", "53688979772");
            sqlStatement = databaseConnection.createStatement();
        } catch (Exception e) {
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
        } catch (Exception e) {
            return 1;
        }

        return 0;
    }

    // Create local objects for database records.
    public void constructDatabaseRecords() {

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
                appointmentRecords.add(new AppointmentRecord(results.getInt("appointmentId"), results.getInt("customerId"),
                        results.getInt("userId"), results.getString("type"), results.getString("url"),
                        results.getTimestamp("start"), results.getTimestamp("end")));
            }

            for (int i = 0; i < customerRecords.size(); i++) {
                combinedCustomerRecords.add(new Customer(customerRecords.get(i)));
            }
        } catch (Exception e) {
            utility.displayError("Error creating local database objects.");
        }
    }

    public void addCustomerRecord(String customerName, String address, String phoneNumber) {
        PreparedStatement statement;
        int addressId;
        int customerId;

        AddressRecord addedAddressRecord;
        CustomerRecord addedCustomerRecord;

        try {
            statement = databaseConnection.prepareStatement("INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

            statement.setString(1, address);
            statement.setString(2, "");
            statement.setInt(3, 1);
            statement.setString(4, "");
            statement.setString(5, phoneNumber);
            statement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(7, "");
            statement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
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

            results = sqlStatement.executeQuery("SELECT customerId FROM customer WHERE customerName = '" + customerName + "'");
            results.next();
            customerId = Integer.parseInt(results.getString("customerId"));

            // Update local record lists.
            addedAddressRecord = new AddressRecord(addressId, address, phoneNumber);
            addedCustomerRecord = new CustomerRecord(customerId, customerName, addressId);

            addressRecords.add(addedAddressRecord);
            customerRecords.add(addedCustomerRecord);
            combinedCustomerRecords.add(new Customer(addedCustomerRecord));

            //constructDatabaseRecords();
        } catch (Exception e) {
            utility.displayError("Error adding customer to database");
        }
    }

    public void checkUpcomingAppointments() {
        Timestamp appointmentTime;

        for (int i = 0; i < combinedCustomerRecords.size(); i++) {
            for (int j = 0; j < combinedCustomerRecords.get(i).getAppointmentList().size(); j++) {
                appointmentTime = combinedCustomerRecords.get(i).getAppointmentList().get(j).getStart();

                if (Duration.between(LocalDateTime.now(), appointmentTime.toLocalDateTime()).toMinutes() <= 15
                        && combinedCustomerRecords.get(i).getAppointmentList().get(j).getUserId() == currentUserId) {

                    utility.displayMessage("Appointment Alert", "You have an appointment with " + combinedCustomerRecords.get(i).getCustomerName() +
                            " within 15 minutes at: " + appointmentTime.toString());
                }
            }
        }
    }

    public void deleteCustomerRecord(Customer customer) {
        try {
            sqlStatement.executeUpdate("DELETE FROM appointment WHERE customerId = " + customer.getCustomerId());
            sqlStatement.executeUpdate("DELETE FROM customer WHERE customerId = " + customer.getCustomerId());
            sqlStatement.executeUpdate("DELETE FROM address WHERE addressId = " + customer.getAddressId());

            // Remove relevant local records from lists.
            for (int i = 0; i < addressRecords.size(); i++) {
                if (addressRecords.get(i).getAddressId() == customer.getAddressId())
                    addressRecords.remove(i);
            }

            for (int i = 0; i < customerRecords.size(); i++) {
                if (customerRecords.get(i).getCustomerId() == customer.getCustomerId())
                    customerRecords.remove(i);
            }

            for (int i = 0; i < appointmentRecords.size(); i++) {
                if (appointmentRecords.get(i).getCustomerId() == customer.getCustomerId())
                    appointmentRecords.remove(i);
            }

            combinedCustomerRecords.remove(customer);
            //constructDatabaseRecords();
        } catch (Exception e) {
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

            // Update relevant local record lists.
            for (int i = 0; i < addressRecords.size(); i++) {
                if (addressRecords.get(i).getAddressId() == customer.getAddressId()) {
                    addressRecords.get(i).setAddress(customer.getAddress());
                    addressRecords.get(i).setPhone(customer.getPhoneNumber());
                }
            }

            for (int i = 0; i < customerRecords.size(); i++) {
                if (customerRecords.get(i).getCustomerId() == customer.getCustomerId())
                    customerRecords.get(i).setCustomerName(customer.getCustomerName());
            }

            combinedCustomerRecords.set(combinedCustomerRecords.indexOf(selectedCustomer), customer);
        } catch (Exception e) {
            utility.displayError("Error updating customer in database.");
        }
    }

    public void addAppointmentRecord(int customerId, int userId, String type, Timestamp start, Timestamp end) {
        PreparedStatement statement;

        int appointmentId;

        try {
            statement = databaseConnection.prepareStatement("INSERT INTO appointment (customerId, userId, title, description, location, contact, type, url, start, end, createDate, " +
                    "createdBy, lastUpdate, lastUpdateBy) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, customerId);
            statement.setInt(2, userId);
            statement.setString(3, "");
            statement.setString(4, "");
            statement.setString(5, "");
            statement.setString(6, "");
            statement.setString(7, type);
            statement.setString(8, "");
            statement.setTimestamp(9, Timestamp.valueOf(start.toLocalDateTime()));
            statement.setTimestamp(10, Timestamp.valueOf(end.toLocalDateTime()));
            statement.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(12, "");
            statement.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            statement.setString(14, "");

            statement.executeUpdate();
            results = statement.getGeneratedKeys();
            results.next();
            appointmentId = results.getInt(1);

            combinedCustomerRecords.get(combinedCustomerRecords.indexOf(selectedCustomer)).getAppointmentList().add(new AppointmentRecord(
                    appointmentId, customerId, userId, type, "", start, end));
        } catch (Exception e) {
            utility.displayError("Error adding appointment to database.");
        }
    }

    public void deleteAppointmentRecord(AppointmentRecord appointment) {
        try {
            sqlStatement.executeUpdate("DELETE FROM appointment WHERE appointmentId = " + appointment.getAppointmentId());

            selectedCustomer.getAppointmentList().remove(appointment);
        } catch (Exception e) {
            utility.displayError("Error deleting appointment from database.");
        }
    }

    public void updateAppointmentRecord(AppointmentRecord appointment) {
        PreparedStatement statement;

        int customerIndex;
        int appointmentIndex;

        try {
            statement = databaseConnection.prepareStatement("UPDATE appointment SET type = ?, start = ?, end = ? WHERE appointmentId = ?");

            statement.setString(1, appointment.getType());
            statement.setTimestamp(2, Timestamp.valueOf(appointment.getStart().toLocalDateTime()));
            statement.setTimestamp(3, Timestamp.valueOf(appointment.getEnd().toLocalDateTime()));
            statement.setInt(4, appointment.getAppointmentId());

            statement.executeUpdate();

            customerIndex = combinedCustomerRecords.indexOf(selectedCustomer);
            appointmentIndex = combinedCustomerRecords.get(customerIndex).getAppointmentList().indexOf(appointment);

            combinedCustomerRecords.get(customerIndex).getAppointmentList().set(appointmentIndex, appointment);
        } catch (Exception e) {
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

    public AppointmentRecord getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(AppointmentRecord selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }
}