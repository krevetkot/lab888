package labs.secondSemester.client.controllers;

import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class MyDialog extends TextInputDialog {

    public Integer askInteger(String text){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Пожалуйста, введите ID дракона:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return(Integer.parseInt(result.get()));
        }
        return null;
    }

    public String askString(String text){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Пожалуйста, введите ID дракона:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()){
            return result.get();
        }
        return null;
    }
}
