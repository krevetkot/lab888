package labs.secondSemester.client.controllers;

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
            System.out.println("УРА НЕУЖЕЛИ МЫ СМОГЛИ ВОЙТИ В АККАУНТ");
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);
        };

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
            System.out.println("УРА НЕУЖЕЛИ МЫ СМОГЛИ ВОЙТИ В АККАУНТ");
            client.setClientID(new ClientIdentification(login, hashedPassword));
            client.getClientID().setAuthorized(true);
        };
    }


}
