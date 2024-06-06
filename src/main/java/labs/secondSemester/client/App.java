package labs.secondSemester.client;

import labs.secondSemester.client.controllers.LoginSignUpController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import labs.secondSemester.client.controllers.MainController;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class App extends Application {
    private Parent authRoot;
    private LoginSignUpController loginSignUpController;
    private MainController mainController;
    private static Client client;
    private Stage stage;

    public static void main(String[] args) {
        try {
            client = new Client("127.0.0.1");
            client.connectServer(1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        launch(args);
    }


    @Override
    public void start(Stage stage) {
        this.stage = stage;
        loginStage();
    }


    public void loginStage(){
        var authLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        authRoot = loadFxml(authLoader);
        Scene scene = new Scene(authRoot);
        stage.setScene(scene);
        loginSignUpController = authLoader.getController();
        loginSignUpController.setClient(client);
        loginSignUpController.setStage(stage);
        stage.show();
    }

    public void mainStage(){
        var mainLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        authRoot = loadFxml(mainLoader);
        Scene scene = new Scene(authRoot);
        stage.setScene(scene);
        mainController = mainLoader.getController();
        mainController.setClient(client);
        stage.show();
    }

    private Parent loadFxml(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
        return null;
    }
}