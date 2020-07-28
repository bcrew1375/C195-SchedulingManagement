package main_menu;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import database.Customer;
import database.Database;
import utility.Utility;

public class AddCustomerController {
    @FXML
    TextField customerNameTextField;
    @FXML
    TextField addressTextField;
    @FXML
    TextField phoneNumberTextField;

    Database database;
    Utility utility;

    @FXML
    private void initialize() {
        database = Database.getInstance();
    }

    @FXML
    private void AddButton() {
        String customerName = customerNameTextField.getText();
        String address = addressTextField.getText();
        String phoneNumber = phoneNumberTextField.getText();

        utility = new Utility();

        try {
            if (customerName.isEmpty()) {
                throw (new Exception("Customer name must not be blank"));
            }
            else if (address.isEmpty()) {
                throw (new Exception("Address must not be blank"));
            }
            else if (phoneNumber.isEmpty()) {
                throw (new Exception("Phone number must not be blank"));
            }
            database.addCustomerRecord(customerName, address, phoneNumber);
            customerNameTextField.getScene().getWindow().hide();
        }
        catch (Exception e) {
            utility.displayError(e.getMessage());
        }

        for (int i = 0; i < database.getCustomerRecords().size(); i++) {
            if (database.getCustomerRecords().get(i).getCustomerName().equals(customerNameTextField.getText())) {
                database.setSelectedCustomer(new Customer(database.getCustomerRecords().get(i)));
            }
        }
    }

    @FXML
    private void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}
