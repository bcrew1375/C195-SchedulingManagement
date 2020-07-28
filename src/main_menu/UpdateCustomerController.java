package main_menu;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import utility.Utility;

public class UpdateCustomerController {
    Database database;
    Utility utility;

    @FXML
    TextField customerNameTextField;
    @FXML
    TextField addressTextField;
    @FXML
    TextField phoneNumberTextField;

    @FXML
    void initialize() {
        database = Database.getInstance();

        customerNameTextField.setText(database.getSelectedCustomer().getCustomerName());
        addressTextField.setText(database.getSelectedCustomer().getAddress());
        phoneNumberTextField.setText(database.getSelectedCustomer().getPhoneNumber());
    }

    @FXML
    void UpdateButton() {
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
            database.getSelectedCustomer().setCustomerName(customerName);
            database.getSelectedCustomer().setAddress(address);
            database.getSelectedCustomer().setPhoneNumber(phoneNumber);

            database.updateCustomerRecord(database.getSelectedCustomer());
            customerNameTextField.getScene().getWindow().hide();
        }
        catch (Exception e) {
            utility.displayError(e.getMessage());
        }

    }

    @FXML
    void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}