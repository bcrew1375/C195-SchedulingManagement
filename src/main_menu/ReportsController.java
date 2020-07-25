package main_menu;

import java.util.Calendar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import database.AppointmentRecord;
import database.Customer;
import database.Database;

public class ReportsController {
    @FXML
    Label typeByMonthLabel;
    @FXML
    ChoiceBox typeByMonthChoiceBox;
    @FXML
    TableView typeByMonthTableView;
    @FXML
    TableColumn typeByMonthTypeColumn;
    @FXML
    TableColumn typeByMonthAmountColumn;
    @FXML
    Label schedulesLabel;
    @FXML
    ChoiceBox schedulesConsultantChoiceBox;
    @FXML
    TableView schedulesTableView;
    @FXML
    TableColumn schedulesTypeColumn;
    @FXML
    TableColumn schedulesStartColumn;
    @FXML
    TableColumn schedulesEndColumn;

    Database database;

    ObservableList<Customer> customerList;
    ObservableList<String> typeByMonthChoices;
    ObservableList<TypeByMonth> typeByMonthAppointmentList;
    ObservableList<AppointmentRecord> schedulesAppointmentList;

    @FXML
    private void initialize() {
        database = Database.getInstance();

        customerList = database.getCombinedCustomerList();

        typeByMonthAppointmentList = FXCollections.observableArrayList();
        typeByMonthChoices = FXCollections.observableArrayList();

        typeByMonthChoices.add("January");
        typeByMonthChoices.add("February");
        typeByMonthChoices.add("March");
        typeByMonthChoices.add("April");
        typeByMonthChoices.add("May");
        typeByMonthChoices.add("June");
        typeByMonthChoices.add("July");
        typeByMonthChoices.add("August");
        typeByMonthChoices.add("September");
        typeByMonthChoices.add("October");
        typeByMonthChoices.add("November");
        typeByMonthChoices.add("December");

        typeByMonthChoiceBox.setItems(typeByMonthChoices);
        typeByMonthChoiceBox.setValue(typeByMonthChoices.get(0));

        typeByMonthTypeColumn.setCellValueFactory(new PropertyValueFactory<TypeByMonth, String>("type"));
        typeByMonthAmountColumn.setCellValueFactory(new PropertyValueFactory<TypeByMonth, Integer>("amount"));
    }

    @FXML
    private void typeByMonthButtonClicked() {
        schedulesLabel.setVisible(false);
        schedulesConsultantChoiceBox.setVisible(false);
        schedulesTableView.setVisible(false);

        typeByMonthLabel.setVisible(true);
        typeByMonthChoiceBox.setVisible(true);
        typeByMonthTableView.setVisible(true);

        typeByMonthChoiceChange();
    }

    @FXML
    private void typeByMonthChoiceChange() {
        Calendar startTime;
        ObservableList<AppointmentRecord> appointmentList;

        typeByMonthAppointmentList.clear();

        startTime = Calendar.getInstance();

        // Loop through every appointment.
        for (int i = 0; i < customerList.size(); i++) {
            appointmentList = customerList.get(i).getAppointmentList();

            for (int j = 0; j < appointmentList.size(); j++) {
                startTime.setTimeInMillis(appointmentList.get(j).getStart().getTime());

                // If an appointment matches the currently selected month.
                if (startTime.get(Calendar.MONTH) == typeByMonthChoiceBox.getSelectionModel().getSelectedIndex()) {
                    if (typeByMonthAppointmentList.size() == 0) {
                        typeByMonthAppointmentList.add(new TypeByMonth(appointmentList.get(j).getType(), 1));
                    }
                    // Loop through the appointment types that already exist. Create a new one if necessary.
                    for (int k = 0; k < typeByMonthAppointmentList.size(); k++) {
                        if (typeByMonthAppointmentList.get(k).getType().toUpperCase().matches(appointmentList.get(j).getType().toUpperCase())) {
                            typeByMonthAppointmentList.get(k).setAmount(typeByMonthAppointmentList.get(k).getAmount() + 1);
                        }
                        else {
                            typeByMonthAppointmentList.add(new TypeByMonth(appointmentList.get(j).getType(), 1));
                        }
                    }
                }
            }
        }

        typeByMonthTableView.setItems(typeByMonthAppointmentList);
    }

    @FXML
    private void schedulesButtonClicked() {
        typeByMonthLabel.setVisible(false);
        typeByMonthChoiceBox.setVisible(false);
        typeByMonthTableView.setVisible(false);

        schedulesLabel.setVisible(true);
        schedulesConsultantChoiceBox.setVisible(true);
        schedulesTableView.setVisible(true);
    }

    @FXML
    private void schedulesConsultantChoiceChange() {
        ObservableList<AppointmentRecord> appointmentList;

        schedulesAppointmentList.clear();

        for (int i = 0; i < customerList.size(); i++) {
            appointmentList = customerList.get(i).getAppointmentList();

            for (int j = 0; j < appointmentList.size(); j++) {
            }
        }
    }

    @FXML
    private void appointmentsPerCustomerButtonClicked() {

    }
}
