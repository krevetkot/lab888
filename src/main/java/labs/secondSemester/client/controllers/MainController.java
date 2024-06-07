package labs.secondSemester.client.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.client.CommandFactory;
import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.exceptions.FailedBuildingException;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.managers.CollectionManager;
import labs.secondSemester.commons.managers.Console;
import labs.secondSemester.commons.objects.Color;
import labs.secondSemester.commons.objects.Country;
import labs.secondSemester.commons.objects.Dragon;
import labs.secondSemester.commons.objects.DragonType;
import labs.secondSemester.commons.objects.forms.DragonForm;
import lombok.Setter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

@Setter
public class MainController {
    //1 - dragon, 2 - person, 3 - coords

    private ArrayList<Dragon> collectionOfDragons;
    private Client client;
    private CommandFactory commandFactory;
    @FXML
    private Label usernameLabel;
    @FXML
    private TableColumn<Dragon, String> ownerColumn1;
    @FXML
    private TableColumn<Dragon, Integer> idColumn1;
    @FXML
    private TableColumn<Dragon, String> nameColumn1;
    @FXML
    private TableColumn<Dragon, String> dateColumn1;
    @FXML
    private TableColumn<Dragon, Long> ageColumn1;
    @FXML
    private TableColumn<Dragon, Long> weightColumn1;
    @FXML
    private TableColumn<Dragon, Boolean> speakingColumn1;
    @FXML
    private TableColumn<Dragon, String> typeColumn1;

    @FXML
    private TableColumn<Dragon, String> nameColumn2;
    @FXML
    private TableColumn<Dragon, String> passportColumn2;
    @FXML
    private TableColumn<Dragon, String> eye_colorColumn2;
    @FXML
    private TableColumn<Dragon, String> hair_colorColumn2;
    @FXML
    private TableColumn<Dragon, String> nationalityColumn2;
    @FXML
    private TableColumn<Dragon, Long> killed_dragonsColumn2;

    @FXML
    private TableColumn<Dragon, Integer> idColumn3;
    @FXML
    private TableColumn<Dragon, Long> xColumn3;
    @FXML
    private TableColumn<Dragon, Float> yColumn3;

    @FXML
    private Tab visualTab;
    @FXML
    private Tab dragonTab;
    @FXML
    private TableView<Dragon> dragonsTable;
    @FXML
    private Tab coordsTab;
    @FXML
    private Tab personTab;

    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button infoButton;
    @FXML
    private Button helpButton;
    @FXML
    private Button filterLessThanKillerButton;
    @FXML
    private Button maxByKillerButton;
    @FXML
    private Button printFieldDescendingAgeButton;
    @FXML
    private Button removeByID;
    @FXML
    private Button removeFirstButton;
    @FXML
    private Button reorderButton;
    @FXML
    private Button executeFileButton;

    private Stage stage;
    private Scene scene;
    private Parent root;

    @FXML
    public void initialize(){

        ownerColumn1.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getOwner()));
        idColumn1.setCellValueFactory(dragon -> new SimpleIntegerProperty(dragon.getValue().getId()).asObject());
        nameColumn1.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getName()));
        dateColumn1.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getCreationDate().toString()));
        ageColumn1.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getAge()).asObject());
        weightColumn1.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getWeight()).asObject());
        speakingColumn1.setCellValueFactory(dragon -> new SimpleBooleanProperty(dragon.getValue().getSpeaking()));
        typeColumn1.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getType().getName()));

        nameColumn2.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller().getName()));
        passportColumn2.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller().getPassportID()));
        eye_colorColumn2.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller().getEyeColor().name()));
        hair_colorColumn2.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller().getHairColor().name()));
        nationalityColumn2.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller().getNationality().name()));
        killed_dragonsColumn2.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getKiller().getCountKilledDragons()).asObject());


        xColumn3.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getCoordinates().getX()).asObject());
        yColumn3.setCellValueFactory(dragon -> new SimpleFloatProperty(dragon.getValue().getCoordinates().getY()).asObject());

        dragonsTable.setItems(FXCollections.observableArrayList(CollectionManager.getCollection()));
//        initializeUser();

    }

    public void initializeUser(){
        usernameLabel.setText("Username: " + client.getClientID().getLogin());
    }

    @FXML
    private void add(ActionEvent e){
        try {
            switchToEditing(e);
            //кароче чекайте
            //мы положим все принятые данные в массив строк
            //пошлем эту красоту в драгон форм.билд
            //получим отвалидированного дракона
            //отправим на сервак, он все сделает. усе
        } catch (IOException ex) {
            throw new RuntimeException(ex);
            //Alert с ошибкой
        }


        try {
            Command command = commandFactory.buildCommand("add");
            DragonForm newDragon = new DragonForm();
            try {

            } catch (FailedBuildingException | IllegalValueException e) {
                Console.print(e.getMessage(), false);
            }
        } catch (IllegalValueException ex) {
            throw new RuntimeException(ex);
            //Alert с ошибкой
        }
    }

    private void switchToEditing(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-view.fxml"));
        root = loader.load();

        MainController mainController = loader.getController();
        mainController.setClient(client);

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


}
