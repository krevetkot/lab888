package labs.secondSemester.client.controllers;

public class Alert extends javafx.scene.control.Alert {
    public Alert(AlertType alertType) {
        super(alertType);
    }

    public void showAlert(String title, String header, String content){
        setTitle(title);
        setHeaderText(header);
        setContentText(content);
        setResizable(true);

        showAndWait();
    }
}
