package labs.secondSemester.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.client.CommandFactory;
import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.commands.Login;
import labs.secondSemester.commons.commands.SignUp;
import labs.secondSemester.commons.managers.CollectionManager;
import labs.secondSemester.commons.network.ClientIdentification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Setter;

import java.io.IOException;

@Setter
public class LoginSignUpController {
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

    public LoginSignUpController(){
        loginField = new TextField();
        passwordField = new TextField();
        registerButton = new Button();
        loginButton = new Button();
    }

    @FXML
    private void initialize(){
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
            System.out.println("УРА НЕУЖЕЛИ МЫ СМОГЛИ ВОЙТИ В АККАУНТ");
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);

            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Confirmation", null, "Вы успешно вошли в аккаунт.");
            try {
                switchToMainScene(e);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, "Вход в аккаунт не выполнен.");
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
            System.out.println("УРА НЕУЖЕЛИ МЫ СМОГЛИ ВОЙТИ В АККАУНТ");
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);

            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Confirmation", null, "Вы успешно вошли в аккаунт.");
            try {
                switchToMainScene(e);
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
        else {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, "Вход в аккаунт не выполнен.");
        }
    }

    public void switchToMainScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        root = loader.load();

        MainController mainController = loader.getController();
        mainController.setClient(client);
        mainController.setStage(stage);
        mainController.setCommandFactory(new CommandFactory(client.getClientID()));

//        mainController.setCollectionOfDragons(client.getCollectionFromServer());

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
