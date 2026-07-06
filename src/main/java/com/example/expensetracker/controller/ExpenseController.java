package com.example.expensetracker.controller;

import com.example.expensetracker.dao.CategoryDAO;
import com.example.expensetracker.dao.ExpenseDAO;
import com.example.expensetracker.model.Category;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.util.AlertHelper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ExpenseController {
    @FXML private TextField amountField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField descField;
    @FXML private Label formTitle;

    private DashboardController parent;
    private int userId;
    private Expense targetExpense;
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    public void setParentController(DashboardController parent, int userId, Expense expense) {
        this.parent = parent;
        this.userId = userId;
        this.targetExpense = expense;

        categoryComboBox.setItems(categoryDAO.getAllCategories());

        if (expense != null) {
            formTitle.setText("Update Transaction Log");
            amountField.setText(String.valueOf(expense.getAmount()));
            datePicker.setValue(LocalDate.parse(expense.getDate()));
            descField.setText(expense.getDescription());
            for (Category c : categoryComboBox.getItems()) {
                if (c.getId() == expense.getCategoryId()) {
                    categoryComboBox.getSelectionModel().select(c);
                    break;
                }
            }
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            Object item = categoryComboBox.getSelectionModel().getSelectedItem();
            Category cat = (Category) item;
            String date = (datePicker.getValue() != null) ? datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE) : "";

            if (amount <= 0 || cat == null || date.isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, amountField.getScene().getWindow(), "Validation Error", "Please provide complete parameters.");
                return;
            }

            boolean ok;
            if (targetExpense == null) {
                ok = expenseDAO.addExpense(userId, cat.getId(), amount, date, descField.getText().trim());
            } else {
                ok = expenseDAO.updateExpense(targetExpense.getId(), cat.getId(), amount, date, descField.getText().trim());
            }

            if (ok) {
                parent.refreshData();
                ((Stage) amountField.getScene().getWindow()).close();
            }
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, amountField.getScene().getWindow(), "Format Error", "Valid numeric decimal required.");
        }
    }
}