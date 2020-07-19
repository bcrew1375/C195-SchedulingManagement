import javafx.application.Application;

import javafx.stage.Stage;
import utility.Utility;

public class C195SchedulingManagement extends Application {
    public static Stage loginStage;

    @Override
    public void start(Stage mainStage) {
        String fxmlFilename;
        Utility loadFxml = new Utility();

        fxmlFilename = "/fxml/Login.fxml";
        mainStage = loadFxml.LoadFXML(fxmlFilename);
        loginStage = mainStage;

        loginStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
