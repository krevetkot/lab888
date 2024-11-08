package labs.secondSemester.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.commands.Login;
import labs.secondSemester.commons.commands.SignUp;
import labs.secondSemester.commons.network.ClientIdentification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Setter
public class LoginSignUpController implements Initializable {
    private Client client;
    @FXML
    private TextField loginField;
    @FXML
    private TextField passwordField;
    @FXML
    private Button registerButton;
    @FXML
    private Button loginButton;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private MyAlert myAlert;

    public LoginSignUpController(){
        loginField = new TextField();
        passwordField = new TextField();
        registerButton = new Button();
        loginButton = new Button();
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle){
        loginField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches(".{0,40}")) {
                loginField.setText(oldValue);
            }
        });
        passwordField.textProperty().addListener((observableValue, oldValue, newValue) -> {
            if (!newValue.matches("\\S*")) {
                passwordField.setText(oldValue);
            }
        });

        myAlert = new MyAlert(Alert.AlertType.NONE);
    }


    @FXML
    private void loginAction(ActionEvent e){
        String login = loginField.getText();
        String hashedPassword = client.encryptStringSHA512(passwordField.getText());
        Command loginCommand = new Login();
        loginCommand.setStringArgument("");
        loginCommand.setClientID(new ClientIdentification(login, hashedPassword));

        client.send(loginCommand);
        if (client.handleLoginResponse()){
            //тут еще хорошо бы выводить всякие дайлоги
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);

            myAlert.showInfo("Вы успешно вошли в аккаунт.");
            try {
                switchToMainScene(e);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            myAlert.showError("Вход в аккаунт не выполнен.");
        }

    }

    @FXML
    private void registerAction(ActionEvent e){
        String login = loginField.getText();
        String hashedPassword = client.encryptStringSHA512(passwordField.getText());
        Command signUpCommand = new SignUp();
        signUpCommand.setStringArgument("");
        signUpCommand.setClientID(new ClientIdentification(login, hashedPassword));

        client.send(signUpCommand);
        if (client.handleLoginResponse()){
            //тут еще хорошо бы выводить всякие дайлоги
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);

            myAlert.showInfo("Вы успешно вошли в аккаунт.");
            try {
                switchToMainScene(e);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            myAlert.showError("Вход в аккаунт не выполнен.");
        }
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/main-view.fxml"));
        root = loader.load();

        MainController mainController = loader.getController();
        mainController.setClient(client);
        mainController.setStage(stage);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
