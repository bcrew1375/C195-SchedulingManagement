package main_menu;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import database.*;
import utility.Utility;

public class MainMenuController {

    @FXML
    private TableView customerTableView;
    @FXML
    private TableView appointmentTableView;

    @FXML
    private TableColumn<CustomerRecord, String> customerNameColumn;
    @FXML
    private TableColumn<AddressRecord, String> customerAddressColumn;
    @FXML
    private TableColumn<CustomerRecord, String> customerPhoneNumberColumn;

    @FXML
    private Label appointmentLabel;
    @FXML
    private TableColumn<AppointmentRecord, String> appointmentTypeColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> appointmentStartColumn;
    @FXML
    private TableColumn<AppointmentRecord, String> appointmentEndColumn;

    private Database database;

    private ObservableList<Customer> customerList;

    private Stage addCustomerStage;
    private Stage updateCustomerStage;
    private Stage addAppointmentStage;
    private Stage updateAppointmentStage;
    private Stage calendarStage;
    private Stage reportsStage;

    private Utility utility;

    @FXML
    private void initialize() {
        database = Database.getInstance();
        utility = new Utility();

        customerList = database.getCombinedCustomerList();

        customerTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        appointmentTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("displayStartTime"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("displayEndTime"));

        customerTableView.setItems(customerList);
        customerTableView.getSelectionModel().select(0);
        refreshAppointmentList();

        // This prevents having to set the selectedCustomer value in the Database class at the beginning of every method that uses it.
        customerTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                database.setSelectedCustomer((Customer) newValue)));

        // This prevents having to set the selectedAppointment value in the Database class at the beginning of every method that uses it.
        appointmentTableView.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) ->
                database.setSelectedAppointment((AppointmentRecord) newValue)));
    }

    @FXML
    private void customersAddButton() {
        addCustomerStage = utility.LoadFXML("/fxml/AddCustomer.fxml");

        addCustomerStage.initModality(Modality.APPLICATION_MODAL);
        addCustomerStage.showAndWait();

        refreshCustomerList();
    }

    @FXML
    private void customersUpdateButton() {
        if (database.getSelectedCustomer() != null) {
            updateCustomerStage = utility.LoadFXML("/fxml/UpdateCustomer.fxml");

            updateCustomerStage.initModality(Modality.APPLICATION_MODAL);
            updateCustomerStage.showAndWait();

            refreshCustomerList();
        }
    }

    @FXML
    private void customersDeleteButton() {
        //database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedCustomer() != null) {
            database.deleteCustomerRecord((Customer) customerTableView.getSelectionModel().getSelectedItem());

            refreshCustomerList();
            refreshAppointmentList();
        }
    }

    @FXML
    private void appointmentAddButton() {
        //database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedCustomer() != null) {
            addAppointmentStage = utility.LoadFXML("/fxml/AddAppointment.fxml");

            addAppointmentStage.initModality(Modality.APPLICATION_MODAL);
            addAppointmentStage.showAndWait();

            refreshCustomerList();
            refreshAppointmentList();
        }
    }

    @FXML
    private void appointmentUpdateButton() {
        //database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());
        //database.setSelectedAppointment((AppointmentRecord) appointmentTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedAppointment() != null) {
            updateAppointmentStage = utility.LoadFXML("/fxml/UpdateAppointment.fxml");

            updateAppointmentStage.initModality(Modality.APPLICATION_MODAL);
            updateAppointmentStage.showAndWait();

            refreshCustomerList();
            refreshAppointmentList();
        }
    }

    @FXML
    private void appointmentDeleteButton() {
        //database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());
        //database.setSelectedAppointment((AppointmentRecord) appointmentTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedAppointment() != null) {
            database.deleteAppointmentRecord((AppointmentRecord) appointmentTableView.getSelectionModel().getSelectedItem());

            refreshCustomerList();
            refreshAppointmentList();
        }
    }

    @FXML
    private void calendarButton() {
        calendarStage = utility.LoadFXML("/fxml/Calendar.fxml");

        calendarStage.initModality(Modality.APPLICATION_MODAL);
        calendarStage.showAndWait();
    }

    @FXML
    private void reportsButton() {
        reportsStage = utility.LoadFXML("/fxml/Reports.fxml");

        reportsStage.initModality(Modality.APPLICATION_MODAL);
        reportsStage.showAndWait();
    }

    @FXML
    private void refreshAppointmentList() {
        ObservableList<AppointmentRecord> allAppointmentList;
        ObservableList<AppointmentRecord> currentUserAppointmentList;

        database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());

        appointmentLabel.setText("Appointments for " + database.getSelectedCustomer().getCustomerName());

        currentUserAppointmentList = FXCollections.observableArrayList();

        if (database.getSelectedCustomer() != null) {
            allAppointmentList = database.getSelectedCustomer().getAppointmentList();

            for (int i = 0; i < allAppointmentList.size(); i++) {
                if (allAppointmentList.get(i).getUserId() == database.getCurrentUserId()) {
                    currentUserAppointmentList.add(allAppointmentList.get(i));
                }
            }

            appointmentTableView.setItems(currentUserAppointmentList);
        } else {
            appointmentTableView.setItems(null);
        }
    }

    @FXML
    private void refreshCustomerList() {
        customerList = database.getCombinedCustomerList();
        customerTableView.setItems(customerList);
    }
}