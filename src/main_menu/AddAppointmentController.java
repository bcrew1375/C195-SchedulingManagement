package main_menu;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import database.Database;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class AddAppointmentController {
    @FXML
    TextField meetingTypeTextField;
    @FXML
    TextField startTextField;
    @FXML
    TextField endTextField;

    Database database;

    @FXML
    void initialize() {
        database = Database.getInstance();
    }

    @FXML
    void AddButton() {
        database.addAppointmentRecord(database.getSelectedCustomer().getCustomerId(), database.getCurrentUserId(),
                meetingTypeTextField.getText(), Timestamp.valueOf(startTextField.getText()), Timestamp.valueOf(endTextField.getText()));
        meetingTypeTextField.getScene().getWindow().hide();
    }

    @FXML
    void CancelButton() {
        meetingTypeTextField.getScene().getWindow().hide();
    }
}
