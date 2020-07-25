package main_menu;

import database.AppointmentRecord;
import database.Customer;
import database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Calendar;

public class CalendarController {
    @FXML
    private TableView appointmentTableView;
    @FXML
    private TableColumn<AppointmentRecord, String> meetingTypeTableColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> startTableColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> endTableColumn;

    Database database;

    ObservableList<AppointmentRecord> allAppointmentsList;
    ObservableList<AppointmentRecord> monthlyAppointmentsList;
    ObservableList<AppointmentRecord> weeklyAppointmentsList;

    @FXML
    private void initialize() {
        meetingTypeTableColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        startTableColumn.setCellValueFactory(new PropertyValueFactory<>("displayStartTime"));
        endTableColumn.setCellValueFactory(new PropertyValueFactory<>("displayEndTime"));

        database = Database.getInstance();

        allAppointmentsList = FXCollections.observableArrayList();
        monthlyAppointmentsList = FXCollections.observableArrayList();
        weeklyAppointmentsList = FXCollections.observableArrayList();

        constructAppointmentLists();

        appointmentTableView.setItems(allAppointmentsList);
    }

    @FXML
    private void allButtonClicked() {
        appointmentTableView.setItems(allAppointmentsList);
    }

    @FXML
    private void monthlyButtonClicked() {
        appointmentTableView.setItems(monthlyAppointmentsList);
    }

    @FXML
    private void weeklyButtonClicked() {
        appointmentTableView.setItems(weeklyAppointmentsList);
    }

    // Preemptively create all viewable appointment lists.
    private void constructAppointmentLists() {
        Calendar startTime;
        Calendar endTime;

        ObservableList<Customer> customerList;
        ObservableList<AppointmentRecord> appointmentList;

        customerList = database.getCombinedCustomerList();
        startTime = Calendar.getInstance();
        endTime = Calendar.getInstance();

        for (int i = 0; i < customerList.size(); i++) {
            appointmentList = customerList.get(i).getAppointmentList();

            for (int j = 0; j < appointmentList.size(); j++) {
                allAppointmentsList.add(appointmentList.get(j));

                startTime.setTimeInMillis(appointmentList.get(j).getStart().getTime());
                endTime.setTimeInMillis(appointmentList.get(j).getStart().getTime());

                // Determine if the appointment falls in the same month as the current one.
                if ((startTime.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) &&
                        (startTime.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH))) {
                    monthlyAppointmentsList.add(appointmentList.get(j));
                }

                // Determine if the appointment falls in the same week as the current one.
                if ((startTime.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) &&
                        (startTime.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)) &&
                        (startTime.get(Calendar.WEEK_OF_MONTH) == Calendar.getInstance().get(Calendar.WEEK_OF_MONTH))) {
                    weeklyAppointmentsList.add(appointmentList.get(j));
                }
            }
        }
    }
}
