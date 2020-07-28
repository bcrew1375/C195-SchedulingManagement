package login;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import database.Database;
import utility.Utility;

public class LoginController {
    @FXML
    private Label titleLabel;
    @FXML
    private Label loginLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField loginTextbox;
    @FXML
    private TextField passwordTextbox;

    @FXML
    private Button loginButton;

    private Stage mainMenuStage;

    private Database database;
    private ResourceBundle language;
    private Utility utility;

    private String noMatchError;
    private String queryError;

    @FXML
    private void initialize() {

        //Locale.setDefault(new Locale("es"));
        //Locale.getDefault();

        utility = new Utility();

        language = ResourceBundle.getBundle("i18n/Login", Locale.getDefault());

        // Change login stage text if the locale is Spanish.
        titleLabel.setText(language.getString("TitleLabel"));
        loginLabel.setText(language.getString("LoginLabel"));
        passwordLabel.setText(language.getString("PasswordLabel"));
        loginTextbox.setPromptText(language.getString("LoginTextbox"));
        passwordTextbox.setPromptText(language.getString("PasswordTextbox"));
        loginButton.setText(language.getString("LoginButton"));

        queryError = language.getString("QueryError");
        noMatchError = language.getString("NoMatchError");

        database = Database.getInstance();
    }

    @FXML
    private void loginButtonClicked() throws IOException {

        database.constructDatabaseRecords();

        try {
            switch (database.checkLoginCredentials(loginTextbox.getText(), passwordTextbox.getText())) {
                case 0: {
                    throw (new Exception(noMatchError));
                }
                case 1: {
                    throw (new Exception(queryError));
                }
                case 2: {
                    recordLogin();
                    mainMenuStage = utility.LoadFXML("/fxml/MainMenu.fxml");
                    loginTextbox.getScene().getWindow().hide();
                    mainMenuStage.show();
                    database.checkUpcomingAppointments();
                    break;
                }

                default: {
                    throw (new Exception("Unknown error logging in."));
                }
            }
        } catch (Exception e) {
                utility.displayError(e.getMessage());
        }
    }

    private void recordLogin() throws IOException {
        FileOutputStream logFileStream;
        String logString;
        Timestamp currentTimestamp;

        currentTimestamp = new Timestamp(System.currentTimeMillis());

        logString = "User " + loginTextbox.getText() + " logged in at " +
                new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(currentTimestamp) + "\r\n";

        if (new File("log.txt").exists()) {
            // If the file already exists, append the new log to the end.
            logFileStream = new FileOutputStream("log.txt", true);
            logFileStream.write(logString.getBytes());
        } else {
            logFileStream = new FileOutputStream("log.txt");
            logFileStream.write(logString.getBytes());
        }
    }
}