package main_menu;

import java.util.Calendar;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import database.AppointmentRecord;
import database.Customer;
import database.Database;
import javafx.scene.control.cell.PropertyValueFactory;
import org.omg.CORBA.portable.ValueFactory;

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

    Database database;

    ObservableList<Customer> customerList;
    ObservableList<String> typeByMonthChoices;
    ObservableList<TypeByMonth> typeByMonthAppointmentList;

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
        typeByMonthLabel.setVisible(true);
        typeByMonthChoiceBox.setVisible(true);
        typeByMonthTableView.setVisible(true);
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

    }

    @FXML
    private void appointmentsPerCustomerButtonClicked() {

    }
}
