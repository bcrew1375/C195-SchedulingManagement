package main_menu;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class UpdateCustomerController {
    Database database;

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
        database.getSelectedCustomer().setCustomerName(customerNameTextField.getText());
        database.getSelectedCustomer().setAddress(addressTextField.getText());
        database.getSelectedCustomer().setPhoneNumber(phoneNumberTextField.getText());

        database.updateCustomerRecord(database.getSelectedCustomer());

        customerNameTextField.getScene().getWindow().hide();
    }

    @FXML
    void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}