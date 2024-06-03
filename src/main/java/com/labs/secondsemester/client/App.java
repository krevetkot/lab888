package com.labs.secondsemester.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {
    private Parent authRoot;
//    private static final Logger logger = LogManager.getLogger(App.class);

    @Override
    public void start(Stage stage) throws IOException {
        var authLoader = new FXMLLoader(getClass().getResource("login-view.fxml"));
        authRoot = loadFxml(authLoader);
//        Group root = new Group();
        Scene scene = new Scene(authRoot);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
            launch(args);
    }

    private Parent loadFxml(FXMLLoader loader) {
        try {
            return loader.load();
        } catch (IOException e) {

//            logger.error("Can't load " + loader, e);
            System.exit(1);
        }
        return null;
    }
}