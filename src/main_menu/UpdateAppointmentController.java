package main_menu;

import database.AppointmentRecord;
import database.Customer;
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
import java.time.LocalDate;
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

    Integer startYear;
    Integer startMonth;
    Integer startDay;
    Integer startHour;
    Integer startMinute;

    Integer endYear;
    Integer endMonth;
    Integer endDay;
    Integer endHour;
    Integer endMinute;

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
        int scheduleConflict;

        try {
            startYear = startDatePicker.getValue().getYear();
            startMonth = startDatePicker.getValue().getMonthValue();
            startDay = startDatePicker.getValue().getDayOfMonth();
            startHour = Integer.parseInt(startHourTextField.getText());
            startMinute = Integer.parseInt(startMinuteTextField.getText());

            if ((startHour < 1 || startHour > 12) || (startMinute < 0 || startMinute > 59)) {
                utility.displayError("Hour must be between 1 and 12. Minute must be between 0 and 59.");
            } else {
                startTime.set(Calendar.HOUR, startHour);
                if (startAmPmChoiceBox.getValue() == "AM") {
                    startTime.set(Calendar.AM_PM, Calendar.AM);
                } else if (startAmPmChoiceBox.getValue() == "PM") {
                    startTime.set(Calendar.AM_PM, Calendar.PM);
                } else {
                    utility.displayError("There was an error with the AM/PM selection.");
                }
            }

            startTime.set(Calendar.MINUTE, startMinute);
            startTime.set(Calendar.YEAR, startYear);
            startTime.set(Calendar.MONTH, startMonth - 1);
            startTime.set(Calendar.DAY_OF_MONTH, startDay);

            endYear = endDatePicker.getValue().getYear();
            endMonth = endDatePicker.getValue().getMonthValue();
            endDay = endDatePicker.getValue().getDayOfMonth();
            endHour = Integer.parseInt(endHourTextField.getText());
            endMinute = Integer.parseInt(endMinuteTextField.getText());

            if ((endHour < 1 || endHour > 12) || (endMinute < 0 || endMinute > 59)) {
                utility.displayError("Hour must be between 1 and 12. Minute must be between 0 and 59.");
            } else {
                endTime.set(Calendar.HOUR, endHour);
                if (endAmPmChoiceBox.getValue() == "AM") {
                    endTime.set(Calendar.AM_PM, Calendar.AM);
                } else if (endAmPmChoiceBox.getValue() == "PM") {
                    endTime.set(Calendar.AM_PM, Calendar.PM);
                } else {
                    utility.displayError("There was an error with the AM/PM selection.");
                }
            }

            endTime.set(Calendar.MINUTE, endMinute);
            endTime.set(Calendar.YEAR, endYear);
            endTime.set(Calendar.MONTH, endMonth - 1);
            endTime.set(Calendar.DAY_OF_MONTH, endDay);

            if (startTime.after(endTime)) {
                utility.displayError("The appointment end time can't be before the start.");
            }
            else {
                scheduleConflict = checkAppointmentConflict();

                if (scheduleConflict == 0) {
                    database.getSelectedAppointment().setType(meetingTypeTextField.getText());
                    database.getSelectedAppointment().setStart(Timestamp.from(startTime.toInstant()));
                    database.getSelectedAppointment().setEnd(Timestamp.from(endTime.toInstant()));

                    database.updateAppointmentRecord(database.getSelectedAppointment());
                } else if (scheduleConflict == 1) {
                    utility.displayError("This appointment falls outside business hours.");
                } else if (scheduleConflict == 2) {
                    utility.displayError("This appointment conflicts with an already scheduled appointment.");
                }
            }
        }
        catch (Exception e) {
            utility.displayError("There was an error setting the appointment.");
        }
    }

    @FXML
    void CancelButton() {
        meetingTypeTextField.getScene().getWindow().hide();
    }

    int checkAppointmentConflict() {
        ObservableList<Customer> customerList;
        ObservableList<AppointmentRecord> appointmentList;
        Timestamp addedAppointmentStart;
        Timestamp addedAppointmentEnd;
        Timestamp currentAppointmentStart;
        Timestamp currentAppointmentEnd;

        customerList = database.getCombinedCustomerList();
        addedAppointmentStart = Timestamp.from(startTime.toInstant());
        addedAppointmentEnd = Timestamp.from(endTime.toInstant());

        // If there is a conflict with business hours.
        if ((startTime.get(Calendar.HOUR_OF_DAY) < 8) || (startTime.get(Calendar.HOUR_OF_DAY) > 16) ||
                (endTime.get(Calendar.HOUR_OF_DAY) < 8) || (endTime.get(Calendar.HOUR_OF_DAY) > 16)) {
            return 1;
        }

        for (int i = 0; i < customerList.size(); i++) {
            appointmentList = customerList.get(i).getAppointmentList();

            for (int j = 0; j < appointmentList.size(); j++) {
                currentAppointmentStart = customerList.get(i).getAppointmentList().get(j).getStart();
                currentAppointmentEnd = customerList.get(i).getAppointmentList().get(j).getEnd();

                // If there is a schedule conflict with other appointments.
                if (((addedAppointmentStart.compareTo(currentAppointmentStart) >= 0) && (addedAppointmentStart.compareTo(currentAppointmentEnd) <= 0))
                        || (addedAppointmentEnd.compareTo(currentAppointmentStart) >= 0) && (addedAppointmentEnd.compareTo(currentAppointmentEnd) <= 0)) {
                    return 2;
                }
            }
        }

        return 0;
    }
}
