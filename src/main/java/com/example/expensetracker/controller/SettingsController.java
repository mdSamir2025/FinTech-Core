package com.example.expensetracker.controller;

import com.example.expensetracker.dao.CategoryDAO;
import com.example.expensetracker.dao.ExpenseDAO;
import com.example.expensetracker.dao.UserDAO;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.User;
import com.example.expensetracker.util.AlertHelper;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class SettingsController {
    @FXML private TextField budgetLimitField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private TextField newCategoryField;
    @FXML private ListView<Category> categoryListView;
    // Add these @FXML variables inside your SettingsController class body
    @FXML private VBox panelBudgetSecurity;
    @FXML private VBox panelCategoryManager;
    @FXML private VBox panelDataReset;

    @FXML
    void showBudgetSecurity(ActionEvent event) {
        panelBudgetSecurity.setVisible(true);
        panelCategoryManager.setVisible(false);
        panelDataReset.setVisible(false);
    }

    @FXML
    void showCategoryManager(ActionEvent event) {
        panelBudgetSecurity.setVisible(false);
        panelCategoryManager.setVisible(true);
        panelDataReset.setVisible(false);
    }

    @FXML
    void showDataReset(ActionEvent event) {
        panelBudgetSecurity.setVisible(false);
        panelCategoryManager.setVisible(false);
        panelDataReset.setVisible(true);
    }
    private final UserDAO userDAO = new UserDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private User user;

    @FXML
    public void initialize() {
        this.user = DashboardController.getCurrentUser();
        budgetLimitField.setText(String.valueOf(user.getMonthlyLimit()));
        refreshCategories();
    }

    private void refreshCategories() {
        categoryListView.setItems(categoryDAO.getAllCategories());
    }

    @FXML
    void handleUpdateBudget(ActionEvent event) {
        try {
            double limit = Double.parseDouble(budgetLimitField.getText());
            if (userDAO.updateMonthlyLimit(user.getId(), limit)) {
                user.setMonthlyLimit(limit);
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, budgetLimitField.getScene().getWindow(), "Success", "Budget Limit Updated.");
            }
        } catch (Exception e) { /* Format Handling */ }
    }

    @FXML
    void handleChangePassword(ActionEvent event) {
        String oldP = oldPasswordField.getText();
        String newP = newPasswordField.getText();
        if(!oldP.isEmpty() && !newP.isEmpty() && userDAO.updatePassword(user.getId(), oldP, newP)) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, oldPasswordField.getScene().getWindow(), "Success", "Password updated successfully.");
            oldPasswordField.clear(); newPasswordField.clear();
        }
    }

    @FXML
    void handleAddCategory(ActionEvent event) {
        String name = newCategoryField.getText().trim();
        if(!name.isEmpty() && categoryDAO.addCategory(name)) {
            refreshCategories();
            newCategoryField.clear();
        }
    }

    @FXML
    void handleDeleteCategory(ActionEvent event) {
        Category selected = categoryListView.getSelectionModel().getSelectedItem();
        if (selected != null && categoryDAO.deleteCategory(selected.getId())) {
            refreshCategories();
        }
    }

    @FXML
    void handleWipeData(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete all transaction statements and history entirely?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();
        if (confirm.getResult() == ButtonType.YES) {
            expenseDAO.wipeData(user.getId());
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, budgetLimitField.getScene().getWindow(), "Cleared", "Data reset completely.");
        }
    }
}