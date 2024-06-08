package labs.secondSemester.client.controllers;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import labs.secondSemester.client.Client;
import labs.secondSemester.client.CommandFactory;
import labs.secondSemester.commons.commands.Add;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

@Setter
public class MainController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    private ArrayList<Dragon> collectionOfDragons;
    private Client client;
    private CommandFactory commandFactory;
    private boolean isEditing;
    @FXML
    private Label usernameLabel;
    @FXML
    private TableColumn<Dragon, String> ownerColumn;
    @FXML
    private TableColumn<Dragon, Integer> idColumn;
    @FXML
    private TableColumn<Dragon, String> nameColumn;
    @FXML
    private TableColumn<Dragon, Long> xColumn;
    @FXML
    private TableColumn<Dragon, Float> yColumn;
    @FXML
    private TableColumn<Dragon, String> dateColumn;
    @FXML
    private TableColumn<Dragon, Long> ageColumn;
    @FXML
    private TableColumn<Dragon, Long> weightColumn;
    @FXML
    private TableColumn<Dragon, Boolean> speakingColumn;
    @FXML
    private TableColumn<Dragon, String> typeColumn;

    @FXML
    private TableColumn<Dragon, String> kNameColumn;
    @FXML
    private TableColumn<Dragon, String> kPassportColumn;
    @FXML
    private TableColumn<Dragon, String> kEyeColorColumn;
    @FXML
    private TableColumn<Dragon, String> kHairColorColumn;
    @FXML
    private TableColumn<Dragon, String> kNationalityColumn;
    @FXML
    private TableColumn<Dragon, Long> kKilledDragonsColumn;

    @FXML
    private Tab visualTab;
    @FXML
    private Tab dragonTab;
    @FXML
    private TableView<Dragon> dragonsTable;

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

    private EditController editController;
    private Dragon currentDragon = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ownerColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getOwner()));
        idColumn.setCellValueFactory(dragon -> new SimpleIntegerProperty(dragon.getValue().getId()).asObject());
        nameColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getName()));
        dateColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getCreationDate().toString()));
        ageColumn.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getAge()).asObject());
        weightColumn.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getWeight()).asObject());
        speakingColumn.setCellValueFactory(dragon -> new SimpleBooleanProperty(dragon.getValue().getSpeaking()));
        typeColumn.setCellValueFactory(dragon -> new SimpleStringProperty(
                dragon.getValue().getType().getName()  != null ?
                        dragon.getValue().getType().getName() :
                        "null"));

        kNameColumn.setCellValueFactory(dragon -> new SimpleStringProperty(
                dragon.getValue().getKiller() != null ?
                        dragon.getValue().getKiller().getName() :
                        "null"));
        kPassportColumn.setCellValueFactory(dragon -> new SimpleStringProperty(
                dragon.getValue().getKiller() != null ?
                        dragon.getValue().getKiller().getPassportID() :
                        "null"));
        kEyeColorColumn.setCellValueFactory(dragon -> new SimpleStringProperty(
                dragon.getValue().getKiller() != null ?
                        dragon.getValue().getKiller().getEyeColor().name() :
                        "null"));
        kHairColorColumn.setCellValueFactory(dragon -> new SimpleStringProperty(
                dragon.getValue().getKiller() != null ?
                        dragon.getValue().getKiller().getHairColor().name() :
                        "null"));
        kNationalityColumn.setCellValueFactory(dragon -> new SimpleStringProperty(dragon.getValue().getKiller() != null ?
                dragon.getValue().getKiller().getNationality().name() :
                "null"));
        kKilledDragonsColumn.setCellValueFactory(dragon -> new SimpleLongProperty(
                dragon.getValue().getKiller() != null ?
                        dragon.getValue().getKiller().getCountKilledDragons() :
                        0).asObject());


        xColumn.setCellValueFactory(dragon -> new SimpleLongProperty(dragon.getValue().getCoordinates().getX()).asObject());
        yColumn.setCellValueFactory(dragon -> new SimpleFloatProperty(dragon.getValue().getCoordinates().getY()).asObject());


        new Thread(() -> {
                try {
                    Platform.runLater(this::fillTable);
                    Platform.runLater(this::initializeUser);
//                    Platform.runLater(this::visualize);
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println(e.getMessage());
                    logger.error(e);
                }
        }).start();


        new Thread(() -> {
            while (true){
                if (!isEditing){
                    try {
                        Platform.runLater(this::updateTable);
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.out.println(e.getMessage());
                        logger.error(e);
                    }
                }
            }

        }).start();


    }

    public void fillTable() {
        try {
            collectionOfDragons = client.getCollectionFromServer();
            ObservableList<Dragon> data = FXCollections.observableArrayList(collectionOfDragons);
            dragonsTable.setItems(data);
        } catch (IllegalStateException e) {
            logger.error(e);
        }
    }

    synchronized public void updateTable(){
        ArrayList<Dragon> tempCollection = client.getCollectionFromServer();
        if (!collectionOfDragons.equals(tempCollection)){
            collectionOfDragons = tempCollection;
        }
        fillTable();
    }
    public void initializeUser(){
        usernameLabel.setText("Username: " + client.getClientID().getLogin());
    }

    @FXML
    private void add(ActionEvent e){
        try {
            switchToEditing(e);
            Command command = commandFactory.buildCommand("add");
            command.setClientID(client.getClientID());
            command.setObjectArgument(currentDragon);

            client.send(command);
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Result", null, client.handleResponse().getResponse().toString());

        } catch (Exception ex) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, ex.getMessage());
        }
    }

    @FXML
    private void clear(ActionEvent e){
        try {
            switchToEditing(e);
            Command command = commandFactory.buildCommand("clear");
            command.setClientID(client.getClientID());
            command.setObjectArgument(currentDragon);

            client.send(command);
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Result", null, client.handleResponse().getResponse().toString());

        } catch (Exception ex) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, ex.getMessage());
        }
    }

    @FXML
    private void info(ActionEvent event){
        try {
            Command command = commandFactory.buildCommand("info");
            command.setClientID(client.getClientID());

            client.send(command);
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Result", null, client.handleResponse().getResponse().toString());
        } catch (IllegalValueException ex) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, ex.getMessage());
        }
    }

    @FXML
    private void help(ActionEvent event){
        try {
            Command command = commandFactory.buildCommand("help");
            command.setClientID(client.getClientID());

            client.send(command);
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Result", null, client.handleResponse().getResponse().toString());
        } catch (IllegalValueException ex) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, ex.getMessage());
        }
    }


    @FXML
    private void printFieldDescendingAge(ActionEvent event){
        try {
            Command command = commandFactory.buildCommand("print_field_descending_age");
            command.setClientID(client.getClientID());

            client.send(command);
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.showAlert("Result", null, client.handleResponse().getResponse().toString());
        } catch (IllegalValueException ex) {
            Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.showAlert("Error", null, ex.getMessage());
        }
    }




    private void switchToEditing(ActionEvent event) throws IOException {
        isEditing = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-view.fxml"));
        root = loader.load();

        editController = loader.getController();
        editController.setClient(client);

        editController.setMainController(this);

//        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage stage1 = new Stage();
        editController.setStage(stage1);
        scene = new Scene(root);
        stage1.setScene(scene);
        stage1.showAndWait();
        isEditing = false;
    }

}
