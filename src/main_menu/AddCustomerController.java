package main_menu;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import database.Customer;
import database.Database;

public class AddCustomerController {
    @FXML
    TextField customerNameTextField;
    @FXML
    TextField addressTextField;
    @FXML
    TextField phoneNumberTextField;

    Database database;

    @FXML
    private void initialize() {
        database = Database.getInstance();
    }

    @FXML
    private void AddButton() {
        database.addCustomerRecord(customerNameTextField.getText(), addressTextField.getText(), phoneNumberTextField.getText());

        for (int i = 0; i < database.getCustomerRecords().size(); i++) {
            if (database.getCustomerRecords().get(i).getCustomerName().equals(customerNameTextField.getText())) {
                database.setSelectedCustomer(new Customer(database.getCustomerRecords().get(i)));
            }
        }

        customerNameTextField.getScene().getWindow().hide();
    }

    @FXML
    private void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}
