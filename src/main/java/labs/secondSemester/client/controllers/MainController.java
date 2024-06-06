package labs.secondSemester.client.controllers;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import labs.secondSemester.client.Client;
import labs.secondSemester.commons.managers.CollectionManager;
import labs.secondSemester.commons.objects.Color;
import labs.secondSemester.commons.objects.Country;
import labs.secondSemester.commons.objects.Dragon;
import labs.secondSemester.commons.objects.DragonType;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

@Setter
public class MainController {
    //1 - dragon, 2 - person, 3 - coords

    private ArrayList<Dragon> collectionOfDragons;
    private Client client;
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
    private TableView<Dragon> tableTable;
    @FXML
    private Tab coordsTab;
    @FXML
    private Tab personTab;

    @FXML
    public void initialize(){
        tableTable.setItems(FXCollections.observableArrayList(CollectionManager.getCollection()));

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

    }


}
