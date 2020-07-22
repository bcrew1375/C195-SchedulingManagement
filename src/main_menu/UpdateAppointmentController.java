package main_menu;

import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import utility.Utility;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Calendar;

public class UpdateAppointmentController {
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

        try {
            startTime.setTimeInMillis(database.getSelectedAppointment().getStart().getTime());
            endTime.setTimeInMillis(database.getSelectedAppointment().getEnd().getTime());

            meetingTypeTextField.setText(database.getSelectedAppointment().getType());

            if (startTime.get(Calendar.HOUR) == 0)
                startHourTextField.setText(Integer.toString(12));
            else
                startHourTextField.setText(Integer.toString(startTime.get(Calendar.HOUR)));
            if (endTime.get(Calendar.HOUR) == 0)
                endHourTextField.setText(Integer.toString(12));
            else
                endHourTextField.setText(Integer.toString(endTime.get(Calendar.HOUR)));

            startMinuteTextField.setText(Integer.toString(startTime.get(Calendar.MINUTE)));
            endMinuteTextField.setText(Integer.toString(endTime.get(Calendar.MINUTE)));

            startAmPmChoiceBox.setItems(amPmChoices);

            endAmPmChoiceBox.setItems(amPmChoices);
            endAmPmChoiceBox.setValue(amPmChoices.get(0));

            if (startTime.get(Calendar.AM_PM) == Calendar.AM)
                startAmPmChoiceBox.setValue(amPmChoices.get(0));
            else if (startTime.get(Calendar.AM_PM) == Calendar.PM)
                startAmPmChoiceBox.setValue(amPmChoices.get(1));

            if (endTime.get(Calendar.AM_PM) == Calendar.AM)
                endAmPmChoiceBox.setValue(amPmChoices.get(0));
            else if (endTime.get(Calendar.AM_PM) == Calendar.PM)
                endAmPmChoiceBox.setValue(amPmChoices.get(1));

            startDatePicker.setValue(LocalDate.ofYearDay(startTime.get(Calendar.YEAR), startTime.get(Calendar.DAY_OF_YEAR)));
            endDatePicker.setValue(LocalDate.ofYearDay(endTime.get(Calendar.YEAR), endTime.get(Calendar.DAY_OF_YEAR)));
        }
        catch (Exception e) {
            utility.displayError("There was an error populating the appointment fields.");
        }
    }

    @FXML
    void UpdateButton() {
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

            database.getSelectedAppointment().setType(meetingTypeTextField.getText());
            database.getSelectedAppointment().setStart(Timestamp.from(startTime.toInstant()));
            database.getSelectedAppointment().setEnd(Timestamp.from(endTime.toInstant()));

            database.updateAppointmentRecord(database.getSelectedAppointment());

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
