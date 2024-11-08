package labs.secondSemester.client.controllers;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import labs.secondSemester.client.Client;
import labs.secondSemester.client.CommandFactory;
import labs.secondSemester.commons.commands.Command;
import labs.secondSemester.commons.exceptions.IllegalValueException;
import labs.secondSemester.commons.managers.CollectionManager;
import labs.secondSemester.commons.network.ClientIdentification;
import labs.secondSemester.commons.network.Response;
import labs.secondSemester.commons.objects.Dragon;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

@Setter
public class MainController implements Initializable {
    private static final Logger logger = LogManager.getLogger(MainController.class);

    private ResourceBundle resourceBundle;
    private Locale locale;

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
    private AnchorPane visualPane;
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
    private Button removeByIDButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button removeFirstButton;
    @FXML
    private Button reorderButton;
    @FXML
    private Button executeFileButton;

    @FXML
    private MenuItem ru;
    @FXML
    private MenuItem mkd;
    @FXML
    private MenuItem lit;
    @FXML
    private MenuItem spaGT;
    @FXML
    private MenuButton languageSwitcher;

    private Stage stage;
    private Scene scene;
    private Parent root;

    private EditController editController;
    private Dragon currentDragon = null;
    private MyDialog myDialog;
    private MyAlert myAlert;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            Platform.runLater(this::initializeTable);
            Platform.runLater(this::fillTable);
            Platform.runLater(this::initializeUser);
        }).start();


        AtomicBoolean work = new AtomicBoolean(true);
        new Thread(() -> {
            while (work.get()){
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


        Thread closingThreads = new Thread(() ->
        {
            work.set(false);
            System.out.println("Все потоки закрыты");
        });
        Runtime.getRuntime().addShutdownHook(closingThreads);
        
        myAlert = new MyAlert(Alert.AlertType.NONE);
        myDialog = new MyDialog();

    }

    private void initializeTable(){
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


    }
    public void fillTable() {
        try {
            CollectionManager.setCollectionOfDragons(client.getCollectionFromServer());
            ObservableList<Dragon> data = FXCollections.observableArrayList(CollectionManager.getCollection());
            dragonsTable.setItems(data);
        } catch (IllegalStateException e) {
            logger.error(e);
        }
        logger.info("Таблица заполнена.");
    }

    synchronized public void updateTable(){
        ArrayList<Dragon> tempCollection = client.getCollectionFromServer();
        if (!CollectionManager.getCollection().equals(tempCollection)){
            logger.info("Таблица обновляется.");
            CollectionManager.setCollectionOfDragons(tempCollection);
            fillTable();
            drawVisualisation();
        }
    }
    public void initializeUser(){
        usernameLabel.setText("Username: " + " " + client.getClientID().getLogin());
        commandFactory = new CommandFactory(client.getClientID());
    }

    @FXML
    private void add(ActionEvent e){
        logger.info("Выполнение команды Add.");
        try {
            switchToEditing(e, null);
            if (currentDragon!=null){
                Command command = commandFactory.buildCommand("add");
                command.setObjectArgument(currentDragon);

                client.send(command);
                myAlert.showResult(client.handleResponse().getResponse().toString());
                updateTable();
            }
        } catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    synchronized private void update(ActionEvent e){
        logger.info("Выполнение команды Update.");
        try {

            Integer dragonID = myDialog.askInteger("Пожалуйста, введите ID дракона:");
            if (dragonID==null){
                return;
            }

            Dragon dragon = CollectionManager.getById(dragonID);
            switchToEditing(e, dragon);
            if (currentDragon!=null){
                Command command = commandFactory.buildCommand("update " + dragonID);
                command.setObjectArgument(currentDragon);

                client.send(command);
                myAlert.showResult(client.handleResponse().getResponse().toString());
                updateTable();
            }
        } catch (NumberFormatException ex) {
            logger.error(ex);
            myAlert.showError("ID должен быть целым числом.");
        }
        catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void removeByID(ActionEvent e){
        logger.info("Выполнение команды RemoveByID.");
        try {

            Integer dragonID = myDialog.askInteger("Пожалуйста, введите ID дракона:");
            if (dragonID==null){
                return;
            }

            Command command = commandFactory.buildCommand("remove_by_id " + dragonID);

            client.send(command);
            myAlert.showResult(client.handleResponse().getResponse().toString());
            updateTable();
        } catch (NumberFormatException ex) {
            logger.error(ex);
            myAlert.showError("ID должен быть целым числом.");
        }
        catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void clear(ActionEvent e){
        logger.info("Выполнение команды Clear.");
        try {
            switchToEditing(e, null);
            Command command = commandFactory.buildCommand("clear");
            command.setClientID(client.getClientID());
            command.setObjectArgument(currentDragon);

            client.send(command);
            myAlert.showResult(client.handleResponse().getResponse().toString());

        } catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void reorder(ActionEvent e){
        logger.info("Выполнение команды Reorder.");
        try {
            Command command = commandFactory.buildCommand("reorder");

            client.send(command);
            myAlert.showResult(client.handleResponse().getResponse().toString());
            fillTable();

        } catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void info(ActionEvent event){
        logger.info("Выполнение команды Info.");
        try {
            Command command = commandFactory.buildCommand("info");

            client.send(command);

            myAlert.showBigResult(client.handleResponse());
        } catch (IllegalValueException ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void help(ActionEvent event){
        logger.info("Выполнение команды Help.");
        try {
            Command command = commandFactory.buildCommand("help");

            client.send(command);
            myAlert.showBigResult(client.handleResponse());
        } catch (IllegalValueException ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }


    @FXML
    private void printFieldDescendingAge(ActionEvent event){
        logger.info("Выполнение команды PrintFieldDescendingAge.");
        try {
            Command command = commandFactory.buildCommand("print_field_descending_age");

            client.send(command);
            myAlert.showBigResult(client.handleResponse());
        } catch (IllegalValueException ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }


    @FXML
    private void maxByKiller(ActionEvent event){
        logger.info("Выполнение команды MaxByKiller.");
        try {
            Command command = commandFactory.buildCommand("max_by_killer");

            client.send(command);
            myAlert.showBigResult(client.handleResponse());
        } catch (IllegalValueException ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }


    @FXML
    private void removeFirst(){
        logger.info("Выполнение команды RemoveFirst.");
        try {
            Command command = commandFactory.buildCommand("remove_first");

            client.send(command);
            myAlert.showResult(client.handleResponse().getResponse().toString());
            updateTable();
        }
        catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }

    @FXML
    private void filterLessThanKiller(){
        myAlert.showResult("Я не успела сделать эту команду *звуки слез*");
    }

    @FXML
    private void executeFile(ActionEvent event){
        logger.info("Выполнение команды ExecuteFile.");
        try {

            String file = myDialog.askString("Пожалуйста, введите полный путь до файла:");
            if (file!=null){
                client.getFileManager().executeFile(file, client.getClientID());

                StringBuilder result = new StringBuilder();
                for (Response resp: client.getFileManager().getResponses()){
                    result.append(resp.getResponse().toString()).append("\n");
                }
                myAlert.showResult(result.toString());
            }
        } catch (Exception ex) {
            logger.error(ex);
            myAlert.showError(ex.getMessage());
        }
    }






    private void switchToEditing(ActionEvent event, Dragon dragon) throws IOException {
        logger.info("Открытие окна редактирования.");
        isEditing = true;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/edit-view.fxml"));
        root = loader.load();

        editController = loader.getController();
        editController.setClient(client);
        editController.setDragon(dragon);
        editController.setMainController(this);

        Stage stage1 = new Stage();
        editController.setStage(stage1);
        scene = new Scene(root);
        stage1.setScene(scene);
        stage1.showAndWait();
        logger.info("Закрытие окна редактирования.");
    }

    private void drawVisualisation(){
        visualPane.getChildren().clear();
        for (Dragon elem: CollectionManager.getCollection()){
            draw(elem);
        }
    }

    @FXML
    private void goToVisual(){
        drawVisualisation();
    }

    private void draw(Dragon dragon){
        int koef = 100;
        Color color = generateColor(dragon.getOwner());
        Circle circle = new Circle(dragon.getCoordinates().getX()*koef, dragon.getCoordinates().getY()*koef, dragon.getWeight(), color);

        circle.setOnMouseClicked(event -> {
            try {
                switchToEditing(new ActionEvent(), dragon);
                if (currentDragon!=null){
                    Command command = commandFactory.buildCommand("update " + dragon.getId());
                    command.setObjectArgument(currentDragon);

                    client.send(command);
                    myAlert.showResult(client.handleResponse().getResponse().toString());
                    updateTable();
                }
            } catch (IOException | IllegalValueException e) {
                logger.error(e);
                myAlert.showError(e.getMessage());
            }
        });

        Label label = new Label(dragon.getName());
        label.setLabelFor(circle);
        label.setLayoutX(circle.getCenterX() - label.getWidth() / 2);
        label.setLayoutY(circle.getCenterY() - label.getHeight() / 2);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), circle);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);


        visualPane.getChildren().addAll(circle, label);

        fadeIn.play();
    }

    public Color generateColor(String client){
        try {
            int x = client.hashCode()*1000000;
            String res = String.valueOf(Math.abs(x));
            String red = "0."+res.charAt(0)+res.charAt(1);
            String green = "0."+res.charAt(2)+res.charAt(3);
            String blue = "0."+res.charAt(4)+res.charAt(5);

            return new Color(Double.parseDouble(red), Double.parseDouble(green), Double.parseDouble(blue), 1);
        } catch (Exception ex){
            myAlert.showError("Трудности с генерацией цвета:" + ex.getMessage());
            return new Color(0, 0, 0, 0);
        }
    }


//    public void changeLanguage() {
//        usernameLabel.setText(resourceBundle.getString("UsernameLabel") + " " + client.getClientID().getLogin());
//
//        helpButton.setText(resourceBundle.getString("Help"));
//        infoButton.setText(resourceBundle.getString("Info"));
//        addButton.setText(resourceBundle.getString("Add"));
//        updateButton.setText(resourceBundle.getString("Update"));
//        filterLessThanKillerButton.setText(resourceBundle.getString("FilterLessThanKillerButton"));
//        clearButton.setText(resourceBundle.getString("Clear"));
//        maxByKillerButton.setText(resourceBundle.getString("MaxByKillerButton"));
//        printFieldDescendingAgeButton.setText(resourceBundle.getString("PrintFieldDescendingAgeButton"));
//        removeByIDButton.setText(resourceBundle.getString("RemoveByIDButton"));
//        removeFirstButton.setText(resourceBundle.getString("RemoveFirstButton"));
//        reorderButton.setText(resourceBundle.getString("ReorderButton"));
//        executeFileButton.setText(resourceBundle.getString("ExecuteFileButton"));
//
//        dragonTab.setText(resourceBundle.getString("DragonTab"));
//        visualTab.setText(resourceBundle.getString("VisualTab"));
//
//        ownerColumn.setText(resourceBundle.getString("Owner"));
//        nameColumn.setText(resourceBundle.getString("Name"));
//        xColumn.setText(resourceBundle.getString("X"));
//        yColumn.setText(resourceBundle.getString("Y"));
//        dateColumn.setText(resourceBundle.getString("CreationDate"));
//        ageColumn.setText(resourceBundle.getString("Age"));
//        weightColumn.setText(resourceBundle.getString("Weight"));
//        speakingColumn.setText(resourceBundle.getString("Speaking"));
//        typeColumn.setText(resourceBundle.getString("Type"));
//
//        kNameColumn.setText(resourceBundle.getString("kNameColumn"));
//        kPassportColumn.setText(resourceBundle.getString("kPassportColumn"));
//        kEyeColorColumn.setText(resourceBundle.getString("kEyeColorColumn"));
//        kHairColorColumn.setText(resourceBundle.getString("kHairColorColumn"));
//        kNationalityColumn.setText(resourceBundle.getString("kNationalityColumn"));
//        kKilledDragonsColumn.setText(resourceBundle.getString("kKilledDragonsColumn"));
//
//        languageSwitcher.setText(resourceBundle.getString("Language"));
//
//        editController.changeLanguage();
//    }



    @FXML
    private void switchRussian(){
        Locale.setDefault(new Locale("ru", "RU"));
        myAlert.showResult("Вы поменяли язык на русский");
    }

    @FXML
    private void switchMakedonian(){
        Locale.setDefault(new Locale("mkd", "MKD"));
        myAlert.showResult("Вы поменяли язык на македонский");
    }

    @FXML
    private void switchLitovian(){
        Locale.setDefault(new Locale("mkd", "MKD"));
        myAlert.showResult("Вы поменяли язык на македонский");
    }

    @FXML
    private void switchGuatemala(){
        Locale.setDefault(new Locale("mkd", "MKD"));
        myAlert.showResult("Вы поменяли язык на македонский");
    }
}
