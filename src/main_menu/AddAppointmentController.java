package main_menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import database.Database;
import utility.Utility;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.util.Calendar;

public class AddAppointmentController {
    @FXML
    TextField meetingTypeTextField;
    @FXML
    TextField startHourTextField;
    @FXML
    TextField startMinuteTextField;
    @FXML
    ChoiceBox startAmPmChoiceBox;
    @FXML
    DatePicker startDatePicker;
    @FXML
    TextField endHourTextField;
    @FXML
    TextField endMinuteTextField;
    @FXML
    ChoiceBox endAmPmChoiceBox;
    @FXML
    DatePicker endDatePicker;

    Database database;
    ObservableList<String> amPmChoices;
    Utility utility;

    Calendar startTime;
    Calendar endTime;

    Integer year;
    Integer month;
    Integer day;
    Integer hour;
    Integer minute;

    @FXML
    void initialize() {
        database = Database.getInstance();
        utility = new Utility();

        amPmChoices = FXCollections.observableArrayList();

        amPmChoices.add("AM");
        amPmChoices.add("PM");

        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        startAmPmChoiceBox.setItems(amPmChoices);
        startAmPmChoiceBox.setValue(amPmChoices.get(0));

        endAmPmChoiceBox.setItems(amPmChoices);
        endAmPmChoiceBox.setValue(amPmChoices.get(0));
    }

    @FXML
    void AddButton() {
        try {
            year = startDatePicker.getValue().getYear();
            month = startDatePicker.getValue().getMonthValue();
            day = startDatePicker.getValue().getDayOfMonth();
            hour = Integer.parseInt(startHourTextField.getText());
            minute = Integer.parseInt(startMinuteTextField.getText());

            if ((hour < 1 || hour > 12) || (minute < 0 || minute > 59)) {
                utility.displayError("Hour must be between 1 and 12. Minute must be between 0 and 59.");
            } else {
                if (startAmPmChoiceBox.getValue() == "AM") {
                    if (hour == 12)
                        hour = 0;
                    startTime.set(Calendar.HOUR_OF_DAY, hour);
                } else if (startAmPmChoiceBox.getValue() == "PM") {
                    if (hour != 12)
                        hour += 12;
                    startTime.set(Calendar.HOUR_OF_DAY, hour + 12);
                } else {
                    utility.displayError("There was an error with the AM/PM selection.");
                }
            }

            startTime.set(Calendar.MINUTE, minute);
            startTime.set(Calendar.YEAR, year);
            startTime.set(Calendar.MONTH, month - 1);
            startTime.set(Calendar.DAY_OF_MONTH, day);

            year = endDatePicker.getValue().getYear();
            month = endDatePicker.getValue().getMonthValue();
            day = endDatePicker.getValue().getDayOfMonth();
            hour = Integer.parseInt(endHourTextField.getText());
            minute = Integer.parseInt(endMinuteTextField.getText());

            if ((hour < 1 || hour > 12) || (minute < 0 || minute > 59)) {
                utility.displayError("Hour must be between 1 and 12. Minute must be between 0 and 59.");
            } else {
                if (endAmPmChoiceBox.getValue() == "AM") {
                    if (hour == 12)
                        hour = 0;
                    endTime.set(Calendar.HOUR_OF_DAY, hour);
                } else if (endAmPmChoiceBox.getValue() == "PM") {
                    if (hour != 12)
                        hour += 12;
                    endTime.set(Calendar.HOUR_OF_DAY, hour);
                } else {
                    utility.displayError("There was an error with the AM/PM selection.");
                }
            }

            endTime.set(Calendar.MINUTE, minute);
            endTime.set(Calendar.YEAR, year);
            endTime.set(Calendar.MONTH, month - 1);
            endTime.set(Calendar.DAY_OF_MONTH, day);

            database.addAppointmentRecord(database.getSelectedCustomer().getCustomerId(), database.getCurrentUserId(),
                    meetingTypeTextField.getText(), Timestamp.from(startTime.toInstant()), Timestamp.from(endTime.toInstant()));
            meetingTypeTextField.getScene().getWindow().hide();
        }
        catch (Exception e) {
            utility.displayError("There was an error setting the appointment.");
        }
    }

    @FXML
    void CancelButton() {
        meetingTypeTextField.getScene().getWindow().hide();
    }
}
