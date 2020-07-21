package main_menu;

import database.Customer;
import database.CustomerRecord;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import database.Database;

public class AddCustomerController {
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
    }

    @FXML
    void AddButton() {
        database.addCustomerRecord(customerNameTextField.getText(), addressTextField.getText(), phoneNumberTextField.getText());

        for (int i = 0; i < database.getCustomerRecords().size(); i++) {
            if (database.getCustomerRecords().get(i).getCustomerName().equals(customerNameTextField.getText())) {
                database.setSelectedCustomer(new Customer(database.getCustomerRecords().get(i)));
            }
        }

        customerNameTextField.getScene().getWindow().hide();
    }

    @FXML
    void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}
