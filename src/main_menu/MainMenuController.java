package main_menu;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableView;

import database.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utility.Utility;

import java.time.LocalDateTime;
import java.util.ArrayList;

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
    private TableColumn<AppointmentRecord, String> appointmentTypeColumn;
    @FXML
    private TableColumn<AppointmentRecord, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<AppointmentRecord, LocalDateTime> appointmentEndColumn;

    Database database;

    ObservableList<Customer> customerList;

    Stage addCustomerStage;
    Stage updateCustomerStage;

    Utility utility;

    @FXML
    void initialize() {
        database = Database.getInstance();
        utility = new Utility();

//        addressList = database.getAddressList();
//        appointmentList = database.getAppointmentList();
//        customerList = database.getCustomerList();
        customerList = database.getCombinedCustomerList();

        customerTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        appointmentTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerPhoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("end"));

        customerTableView.setItems(customerList);
    }

    @FXML
    void customersAddButton() {
        addCustomerStage = utility.LoadFXML("/fxml/AddCustomer.fxml");

        addCustomerStage.initModality(Modality.WINDOW_MODAL);
        addCustomerStage.showAndWait();

        refreshCustomerList();
    }

    @FXML
    void customersUpdateButton() {
        database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedCustomer() != null) {
            updateCustomerStage = utility.LoadFXML("/fxml/UpdateCustomer.fxml");

            updateCustomerStage.initModality(Modality.WINDOW_MODAL);
            updateCustomerStage.showAndWait();

            refreshCustomerList();
        }
    }

    @FXML
    void customersDeleteButton() {
        database.setSelectedCustomer((Customer) customerTableView.getSelectionModel().getSelectedItem());

        if (database.getSelectedCustomer() != null) {
            database.deleteCustomerRecord((Customer) customerTableView.getSelectionModel().getSelectedItem());

            refreshCustomerList();
        }
    }

    @FXML
    void appointmentAddButton() {

    }

    @FXML
    void appointmentUpdateButton() {

    }

    @FXML
    void appointmentDeleteButton() {

    }

    @FXML
    void updateAppointmentList() {
        Customer selectedCustomer;
        ObservableList customerAppointments;

        selectedCustomer = (Customer) customerTableView.getSelectionModel().getSelectedItem();

        appointmentTableView.setItems(selectedCustomer.getAppointmentList());
    }

    void refreshCustomerList() {
        database.constructDatabaseRecords();
        customerList = database.getCombinedCustomerList();
        customerTableView.setItems(customerList);
    }
}
