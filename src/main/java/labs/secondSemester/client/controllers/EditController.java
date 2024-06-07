package labs.secondSemester.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import labs.secondSemester.client.Client;
import labs.secondSemester.commons.objects.Color;
import labs.secondSemester.commons.objects.Country;
import labs.secondSemester.commons.objects.DragonType;
import lombok.Setter;

@Setter
public class EditController {
    private Client client;

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


}
