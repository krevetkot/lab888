package labs.secondSemester.client.controllers;

public class MyAlert extends javafx.scene.control.Alert {

    public MyAlert(AlertType alertType) {
        super(alertType);
    }

    public void showResult(String content){
        setTitle("Result");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.INFORMATION);

        showAndWait();
    }

    public void showError(String content){
        setTitle("Error");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.ERROR);

        showAndWait();
    }

    public void showInfo(String content){
        setTitle("Information");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.INFORMATION);

        showAndWait();
    }
}
