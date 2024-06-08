package labs.secondSemester.client.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.client.CommandFactory;
import labs.secondSemester.commons.exceptions.FailedBuildingException;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.objects.Color;
import labs.secondSemester.commons.objects.Country;
import labs.secondSemester.commons.objects.DragonType;
import labs.secondSemester.commons.objects.forms.DragonForm;
import javafx.collections.FXCollections;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

@Setter
public class EditController {
    private Client client;
    private Stage stage;
    private Parent root;
    private Scene scene;
    private MainController mainController;

    @FXML
    private TextField dragonName;
    @FXML
    private TextField coordX;
    @FXML
    private TextField coordY;
    @FXML
    private TextField age;
    @FXML
    private TextField weight;
    @FXML
    private CheckBox speaking;
    @FXML
    private ComboBox<DragonType> dragonType;
    @FXML
    private CheckBox excistingKiller;

    @FXML
    private TextField killerName;
    @FXML
    private TextField passportID;
    @FXML
    private ComboBox<Color> eyeColor;
    @FXML
    private ComboBox<Color> hairColor;
    @FXML
    private ComboBox<Country> nationality;
    @FXML
    private TextField killedDragons;


    @FXML
    private void initialize(){
        dragonType.setItems(FXCollections.observableArrayList(DragonType.values()));
        eyeColor.setItems(FXCollections.observableArrayList(Color.values()));
        hairColor.setItems(FXCollections.observableArrayList(Color.values()));
        nationality.setItems(FXCollections.observableArrayList(Country.values()));
    }
    @FXML
    public void saveEditing(ActionEvent event){
        String dragonData = "";
        dragonData += dragonName.getText() +  "\n";
        dragonData += coordX.getText() +  "\n";
        dragonData += coordY.getText() +  "\n";
        dragonData += age.getText() +  "\n";
        dragonData += weight.getText() +  "\n";
        dragonData += speaking.isSelected() +  "\n";
        dragonData += dragonType.getValue().getName() +  "\n";
        dragonData += excistingKiller.isSelected() +  "\n";
        if (excistingKiller.isSelected()){
            dragonData += killerName.getText() +  "\n";
            dragonData += passportID.getText() +  "\n";
            dragonData += eyeColor.getValue() +  "\n";
            dragonData += hairColor.getValue() +  "\n";
            dragonData += nationality.getValue() +  "\n";
            dragonData += killedDragons.getText() +  "\n";
        }

        DragonForm dragonForm = new DragonForm();
        try {
            mainController.setCurrentDragon(dragonForm.build(new Scanner(dragonData), true));
        } catch (IllegalValueException | FailedBuildingException e) {
            throw new RuntimeException(e);
        }
        stage.close();

    }


}
