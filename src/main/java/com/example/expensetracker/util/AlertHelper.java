package com.example.expensetracker.util;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class AlertHelper {
    public static void showAlert(Alert.AlertType type, Window owner, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.initOwner(owner);
        alert.show();
    }
}