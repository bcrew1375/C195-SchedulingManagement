package main_menu;

import java.util.Calendar;

import database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    @FXML
    TableView appointmentsPerCustomerTableView;
    @FXML
    TableColumn appointmentsPerCustomerNameColumn;
    @FXML
    TableColumn appointmentsPerCustomerAmountColumn;
    Database database;

    ObservableList<Customer> customerList;
    ObservableList<String> typeByMonthChoices;

    ObservableList<UserRecord> currentUsersList;

    @FXML
    private void initialize() {
        ObservableList<String> userNames;

        database = Database.getInstance();

        customerList = database.getCombinedCustomerList();
        currentUsersList = database.getUserList();
        userNames = FXCollections.observableArrayList();

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

        schedulesTypeColumn.setCellValueFactory(new PropertyValueFactory<AppointmentRecord, String>("type"));
        schedulesStartColumn.setCellValueFactory(new PropertyValueFactory<AppointmentRecord, String>("displayStartTime"));
        schedulesEndColumn.setCellValueFactory(new PropertyValueFactory<AppointmentRecord, String>("displayEndTime"));

        appointmentsPerCustomerNameColumn.setCellValueFactory(new PropertyValueFactory<AppointmentsPerCustomer, String>("customerName"));
        appointmentsPerCustomerAmountColumn.setCellValueFactory(new PropertyValueFactory<AppointmentsPerCustomer, Integer>("amount"));

        for (int i = 0; i < currentUsersList.size(); i++) {
            userNames.add(currentUsersList.get(i).getUserName());
        }
        schedulesConsultantChoiceBox.setItems(userNames);
    }

    @FXML
    private void typeByMonthButtonClicked() {
        typeByMonthLabel.setVisible(true);
        typeByMonthChoiceBox.setVisible(true);
        typeByMonthTableView.setVisible(true);

        schedulesLabel.setVisible(false);
        schedulesConsultantChoiceBox.setVisible(false);
        schedulesTableView.setVisible(false);

        appointmentsPerCustomerTableView.setVisible(false);

        typeByMonthChoiceChange();
    }

    @FXML
    private void typeByMonthChoiceChange() {
        Calendar startTime;
        ObservableList<AppointmentRecord> appointmentList;
        ObservableList<TypeByMonth> typeByMonthAppointmentList;

        typeByMonthAppointmentList = FXCollections.observableArrayList();

        int typeByMonthIndex;
        boolean newType;

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
                        typeByMonthAppointmentList.add(new TypeByMonth(appointmentList.get(j).getType()));
                    }
                    typeByMonthIndex = typeByMonthAppointmentList.size();
                    newType = true;
                    // Loop through the appointment types that already exist in the list. Create a new one if necessary.
                    for (int k = 0; k < typeByMonthIndex; k++) {
                        if (typeByMonthAppointmentList.get(k).getType().toUpperCase().equals(appointmentList.get(j).getType().toUpperCase())) {
                            newType = false;
                            typeByMonthAppointmentList.get(k).setAmount(typeByMonthAppointmentList.get(k).getAmount() + 1);
                        }
                    }
                    if (newType == true) {
                        typeByMonthAppointmentList.add(new TypeByMonth(appointmentList.get(j).getType()));
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

        appointmentsPerCustomerTableView.setVisible(false);
    }

    @FXML
    private void schedulesConsultantChoiceChange() {
        ObservableList<AppointmentRecord> appointmentList;
        ObservableList<AppointmentRecord> schedulesAppointmentList;

        schedulesAppointmentList = FXCollections.observableArrayList();

        UserRecord currentUser = currentUsersList.get(schedulesConsultantChoiceBox.getSelectionModel().getSelectedIndex());

        schedulesAppointmentList.clear();

        for (int i = 0; i < customerList.size(); i++) {
            appointmentList = customerList.get(i).getAppointmentList();

            for (int j = 0; j < appointmentList.size(); j++) {
                if (appointmentList.get(j).getUserId() == currentUser.getUserId()) {
                    schedulesAppointmentList.add(appointmentList.get(j));
                }
            }
        }

        schedulesTableView.setItems(schedulesAppointmentList);
    }

    @FXML
    private void appointmentsPerCustomerButtonClicked() {
        ObservableList<AppointmentsPerCustomer> appointmentsPerCustomersList;

        typeByMonthLabel.setVisible(false);
        typeByMonthChoiceBox.setVisible(false);
        typeByMonthTableView.setVisible(false);

        schedulesLabel.setVisible(false);
        schedulesConsultantChoiceBox.setVisible(false);
        schedulesTableView.setVisible(false);

        appointmentsPerCustomerTableView.setVisible(true);

        appointmentsPerCustomersList = FXCollections.observableArrayList();

        appointmentsPerCustomersList.clear();

        for (int i = 0; i < customerList.size(); i++) {
            appointmentsPerCustomersList.add(new AppointmentsPerCustomer(customerList.get(i).getCustomerName()));
            appointmentsPerCustomersList.get(i).setAmount(customerList.get(i).getAppointmentList().size());
        }

        appointmentsPerCustomerTableView.setItems(appointmentsPerCustomersList);
    }
}
