package com.example.expensetracker.controller;

import com.example.expensetracker.Main;
import com.example.expensetracker.dao.UserDAO;
import com.example.expensetracker.model.User;
import com.example.expensetracker.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(), "Error", "Fill in all fields.");
            return;
        }

        User user = userDAO.loginUser(username, password);
        if (user != null) {
            DashboardController.setCurrentUser(user);
            try {
                Stage stage = (Stage) loginButton.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("dashboard.fxml"));
                stage.setScene(new Scene(loader.load(), 850, 600));
                stage.setTitle("Dashboard - " + user.getUsername());
                stage.setResizable(true);
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, loginButton.getScene().getWindow(), "Failed", "Invalid Credentials.");
        }
    }

    @FXML
    public void handleNavigateToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) loginButton.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("register.fxml"));
            stage.setScene(new Scene(loader.load(), 380, 450));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}