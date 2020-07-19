package main_menu;

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
        customerNameTextField.getScene().getWindow().hide();
    }

    @FXML
    void CancelButton() {
        customerNameTextField.getScene().getWindow().hide();
    }
}
