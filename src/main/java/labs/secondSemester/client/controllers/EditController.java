package labs.secondSemester.client.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.commons.exceptions.FailedBuildingException;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.objects.Color;
import labs.secondSemester.commons.objects.Country;
import labs.secondSemester.commons.objects.Dragon;
import labs.secondSemester.commons.objects.DragonType;
import labs.secondSemester.commons.objects.forms.DragonForm;
import javafx.collections.FXCollections;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;

@Setter
public class EditController implements Initializable {
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
    private CheckBox existingKiller;

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

    private MyAlert myAlert;

    private Dragon dragon; //для апдейта

    private static final Logger logger = LogManager.getLogger(EditController.class);


    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        dragonType.setItems(FXCollections.observableArrayList(DragonType.values()));
        eyeColor.setItems(FXCollections.observableArrayList(Color.values()));
        hairColor.setItems(FXCollections.observableArrayList(Color.values()));
        nationality.setItems(FXCollections.observableArrayList(Country.values()));

        new Thread(() -> {
            if (dragon!=null) {
                Platform.runLater(this::fillFields);
            }
        }).start();

        myAlert = new MyAlert(null);
    }


    private void fillFields(){
        dragonName.setText(dragon.getName());
        coordX.setText(String.valueOf(dragon.getCoordinates().getX()));
        coordY.setText(String.valueOf(dragon.getCoordinates().getY()));
        age.setText(String.valueOf(dragon.getAge()));
        weight.setText(String.valueOf(dragon.getWeight()));
        speaking.setSelected(dragon.getSpeaking());
        if (dragon.getType()!=null){
            dragonType.setValue(dragon.getType());
        }
        if (dragon.getKiller()!=null){
            existingKiller.setSelected(true);
            killerName.setText(dragon.getKiller().getName());
            passportID.setText(dragon.getKiller().getPassportID());
            eyeColor.setValue(dragon.getKiller().getEyeColor());
            hairColor.setValue(dragon.getKiller().getHairColor());
            nationality.setValue(dragon.getKiller().getNationality());
            killedDragons.setText(String.valueOf(dragon.getKiller().getCountKilledDragons()));
        } else {
            existingKiller.setSelected(false);
        }
    }


    @FXML
    public void saveEditing(ActionEvent event){
        String dragonData = "";
        try {
            dragonData += dragonName.getText() + "\n";
            dragonData += coordX.getText() + "\n";
            dragonData += coordY.getText() + "\n";
            dragonData += age.getText() + "\n";
            dragonData += weight.getText() + "\n";
            dragonData += speaking.isSelected() + "\n";
            dragonData += dragonType.getValue().getName() + "\n";
            dragonData += existingKiller.isSelected() + "\n";
            if (existingKiller.isSelected()) {
                dragonData += killerName.getText() + "\n";
                dragonData += passportID.getText() + "\n";
                dragonData += eyeColor.getValue() + "\n";
                dragonData += hairColor.getValue() + "\n";
                dragonData += nationality.getValue() + "\n";
                dragonData += killedDragons.getText() + "\n";
            }

            DragonForm dragonForm = new DragonForm();
            Dragon newDragon = dragonForm.build(new Scanner(dragonData), true);
            newDragon.setOwner(client.getClientID().getLogin());
            mainController.setCurrentDragon(newDragon);
            mainController.setEditing(false);
            stage.close();

        } catch (NullPointerException | IllegalValueException | FailedBuildingException e) {
            logger.error(e);
            myAlert.showError("Проверьте правильность заполнения всех полей!");
        }
    }
}
