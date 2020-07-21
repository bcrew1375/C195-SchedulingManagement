package main_menu;

import database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

public class UpdateAppointmentController {
    Database database;

    @FXML
    TextField meetingTypeTextField;
    @FXML
    TextField startTextField;
    @FXML
    TextField endTextField;

    @FXML
    void initialize() {
        database = Database.getInstance();

        meetingTypeTextField.setText(database.getSelectedAppointment().getType());
        startTextField.setText(database.getSelectedAppointment().getStart().toString());
        endTextField.setText(database.getSelectedAppointment().getEnd().toString());
    }

    @FXML
    void UpdateButton() {
        database.getSelectedAppointment().setType(meetingTypeTextField.getText());
        database.getSelectedAppointment().setStart(Timestamp.valueOf(startTextField.getText()));
        database.getSelectedAppointment().setEnd(Timestamp.valueOf(endTextField.getText()));

        database.updateAppointmentRecord(database.getSelectedAppointment());

        meetingTypeTextField.getScene().getWindow().hide();
    }

    @FXML
    void CancelButton() {
        meetingTypeTextField.getScene().getWindow().hide();
    }
}
