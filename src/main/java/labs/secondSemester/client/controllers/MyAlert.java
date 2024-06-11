package labs.secondSemester.client.controllers;

import labs.secondSemester.commons.network.Response;

import java.util.ResourceBundle;

public class MyAlert extends javafx.scene.control.Alert {
//    private ResourceBundle resourceBundle;

//    public MyAlert(AlertType alertType, ResourceBundle resourceBundle) {
//        super(alertType);
//        this.resourceBundle = ResourceBundle.getBundle("locale");
//    }

    public MyAlert(AlertType alertType) {
        super(alertType);
    }


    public void showResult(String content){
//        setTitle(resourceBundle.getString("Result"));
        setTitle("Result");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.INFORMATION);

        showAndWait();
    }

    public void showBigResult(Response response){
        setTitle("Result");
        setHeaderText(null);
        setResizable(true);
        setAlertType(AlertType.INFORMATION);

        String content = "";
        for (String elem: response.getResponse()){
            content += elem + '\n';
        }
        setContentText(content);
        showAndWait();
    }

    public void showError(String content){
//        setTitle(resourceBundle.getString("Error"));
        setTitle("Error");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.ERROR);

        showAndWait();
    }

    public void showInfo(String content){
//        setTitle(resourceBundle.getString("Information"));
        setTitle("Information");
        setHeaderText(null);
        setContentText(content);
        setResizable(true);
        setAlertType(AlertType.INFORMATION);

        showAndWait();
    }
}
