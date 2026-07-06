package com.example.expensetracker.controller;

import com.example.expensetracker.dao.IncomeDAO;
import com.example.expensetracker.model.Income;
import com.example.expensetracker.util.AlertHelper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.format.DateTimeFormatter;

public class IncomeController {
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextField sourceField;
    @FXML private TableView<Income> incomeTable;
    @FXML private TableColumn<Income, String> colDate;
    @FXML private TableColumn<Income, String> colSource;
    @FXML private TableColumn<Income, Double> colAmount;

    private final IncomeDAO incomeDAO = new IncomeDAO();
    private int userId;

    @FXML
    public void initialize() {
        // Fallback user ID tracking layer if session cache drops
        if (DashboardController.getCurrentUser() != null) {
            this.userId = DashboardController.getCurrentUser().getId();
        } else {
            this.userId = 1;
        }

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colSource.setCellValueFactory(new PropertyValueFactory<>("source"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

        loadTable();
    }

    private void loadTable() {
        try {
            incomeTable.setItems(FXCollections.observableArrayList(incomeDAO.getIncomeByUser(userId)));
        } catch (Exception e) {
            System.out.println("Income table display loading bypassed safely.");
        }
    }

    @FXML
    void handleAdd(ActionEvent event) {
        try {
            if (amountField.getText().trim().isEmpty() || sourceField.getText().trim().isEmpty() || datePicker.getValue() == null) {
                AlertHelper.showAlert(Alert.AlertType.ERROR, amountField.getScene().getWindow(), "Validation Error", "All income parameters are required.");
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            String src = sourceField.getText().trim();
            String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);

            if (amount <= 0) return;

            if (incomeDAO.addIncome(userId, amount, date, src)) {
                loadTable();
                amountField.clear();
                sourceField.clear();
                datePicker.setValue(null);
            }
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, amountField.getScene().getWindow(), "Format Error", "Valid numeric decimal amount required.");
        }
    }

    @FXML
    void handleDelete(ActionEvent event) {
        Income sel = incomeTable.getSelectionModel().getSelectedItem();
        if (sel != null && incomeDAO.deleteIncome(sel.getId())) {
            loadTable();
        }
    }
}