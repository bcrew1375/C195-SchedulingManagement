package login;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import utility.Utility;
import database.Database;

public class LoginController {
    @FXML
    Label titleLabel;
    @FXML
    Label loginLabel;
    @FXML
    Label passwordLabel;
    @FXML
    TextField loginTextbox;
    @FXML
    TextField passwordTextbox;

    Stage mainMenuStage;

    Database database;
    ResourceBundle language;
    Utility utility;

    String queryError;
    String noMatchError;

    @FXML
    void initialize() {

        //Locale.setDefault(new Locale("es_es"));

        utility = new Utility();

        language = ResourceBundle.getBundle("i18n/Login", Locale.getDefault());

        String lang = language.getLocale().getDisplayLanguage();

        // Change login stage text if the locale is Spanish.
        if (language.getLocale().getDisplayLanguage().equals("es_es") || language.getLocale().getDisplayLanguage().equals("English")) {
            titleLabel.setText(language.getString("TitleLabel"));
            loginLabel.setText(language.getString("LoginLabel"));
            passwordLabel.setText(language.getString("PasswordLabel"));
            loginTextbox.setPromptText(language.getString("LoginTextbox"));
            passwordTextbox.setPromptText(language.getString("PasswordTextbox"));

            queryError = language.getString("QueryError");
            noMatchError = language.getString("NoMatchError");
        }

        database = Database.getInstance();
    }

    @FXML
    void loginButtonClicked() {

        switch (database.checkLoginCredentials(loginTextbox.getText(), passwordTextbox.getText())) {
            case 0:
            { utility.displayError(noMatchError); break;}
            case 1:
            { utility.displayError(queryError); break;}
            case 2: {
                recordLogin();
                database.constructDatabaseRecords();
                mainMenuStage = utility.LoadFXML("/fxml/MainMenu.fxml");
                loginTextbox.getScene().getWindow().hide();
                mainMenuStage.show();
                database.checkUpcomingAppointments();
                break;
            }

            default: {
            }
        }
    }

    void recordLogin() {
        FileOutputStream logFileStream;
        String logString;
        Timestamp currentTimestamp;

        currentTimestamp = new Timestamp(System.currentTimeMillis());

        try {
            logString = "User " + loginTextbox.getText() + " logged in at " +
                    new SimpleDateFormat("hh:mm aa  MM-dd-yyyy").format(currentTimestamp) + "\r\n";

            if (new File("log.txt").exists()) {
                // If the file already exists, append the new log to the end.
                logFileStream = new FileOutputStream("log.txt", true);
                logFileStream.write(logString.getBytes());
            }
            else {
                logFileStream = new FileOutputStream("log.txt");
                logFileStream.write(logString.getBytes());
            }
        }
        catch (Exception e) {

        }
    }
}