package com.example.expensetracker.controller;

import com.example.expensetracker.Main;
import com.example.expensetracker.dao.UserDAO;
import com.example.expensetracker.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button registerButton;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleRegister(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirm = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, registerButton.getScene().getWindow(), "Error", "Fields cannot be empty.");
            return;
        }
        if (!password.equals(confirm)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, registerButton.getScene().getWindow(), "Error", "Passwords mismatch.");
            return;
        }

        if (userDAO.registerUser(username, password)) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, registerButton.getScene().getWindow(), "Success", "Registered!");
            handleNavigateToLogin(null);
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, registerButton.getScene().getWindow(), "Error", "Registration Failed.");
        }
    }

    @FXML
    public void handleNavigateToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) registerButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("login.fxml"));
            stage.setScene(new Scene(loader.load(), 380, 450));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}