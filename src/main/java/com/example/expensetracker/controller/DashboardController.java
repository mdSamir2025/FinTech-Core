package com.example.expensetracker.controller;

import com.example.expensetracker.Main;
import com.example.expensetracker.dao.ExpenseDAO;
import com.example.expensetracker.dao.IncomeDAO;
import com.example.expensetracker.model.Expense;
import com.example.expensetracker.model.Income;
import com.example.expensetracker.model.User;
import com.example.expensetracker.util.AlertHelper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DashboardController {
    @FXML private Label netBalanceLabel;
    @FXML private Label totalExpenseLabel;
    @FXML private Label totalIncomeLabel;
    @FXML private Label todayExpenseLabel;
    @FXML private Label monthExpenseLabel;
    @FXML private Label budgetAlertLabel;
    @FXML private TextField searchField;

    @FXML private TableView<Expense> expenseTable;
    @FXML private TableColumn<Expense, String> colDate;
    @FXML private TableColumn<Expense, String> colCategory;
    @FXML private TableColumn<Expense, Double> colAmount;
    @FXML private TableColumn<Expense, String> colDesc;

    private static User currentUser;
    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private final IncomeDAO incomeDAO = new IncomeDAO();
    private ObservableList<Expense> masterExpenseList = FXCollections.observableArrayList();

    public static void setCurrentUser(User user) { currentUser = user; }
    public static User getCurrentUser() { return currentUser; }

    @FXML
    public void initialize() {
        if (currentUser == null) return;

        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));

        // FIXED: Using standard policy matching your working income window configuration
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        refreshData();

        searchField.textProperty().addListener((observable, oldVal, newVal) -> filterExpenses(newVal));

        Platform.runLater(() -> {
            Stage stage = (Stage) expenseTable.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            }
        });
    }

    public void refreshData() {
        List<Expense> expenses = expenseDAO.getExpensesByUser(currentUser.getId());
        masterExpenseList.setAll(expenses);
        expenseTable.setItems(masterExpenseList);

        String todayStr = LocalDate.now().toString();
        String currentMonthPrefix = LocalDate.now().toString().substring(0, 7);

        double totalExpense = masterExpenseList.stream().mapToDouble(Expense::getAmount).sum();
        double todayExpense = masterExpenseList.stream().filter(e -> e.getDate().equals(todayStr)).mapToDouble(Expense::getAmount).sum();
        double monthlyExpense = masterExpenseList.stream().filter(e -> e.getDate().startsWith(currentMonthPrefix)).mapToDouble(Expense::getAmount).sum();

        double totalIncome = incomeDAO.getIncomeByUser(currentUser.getId()).stream().mapToDouble(Income::getAmount).sum();
        double netBalance = totalIncome - totalExpense;

        totalExpenseLabel.setText("$" + String.format("%.2f", totalExpense));
        totalIncomeLabel.setText("$" + String.format("%.2f", totalIncome));
        netBalanceLabel.setText("$" + String.format("%.2f", netBalance));
        todayExpenseLabel.setText("$" + String.format("%.2f", todayExpense));
        monthExpenseLabel.setText("$" + String.format("%.2f", monthlyExpense));

        if (currentUser.getMonthlyLimit() > 0 && monthlyExpense >= currentUser.getMonthlyLimit()) {
            budgetAlertLabel.setText("⚠️ CRITICAL: Monthly Budget Exceeded!");
            budgetAlertLabel.setStyle("-fx-text-fill: #EF4444; -fx-font-weight: bold;");
        } else if (currentUser.getMonthlyLimit() > 0 && monthlyExpense >= (currentUser.getMonthlyLimit() * 0.85)) {
            budgetAlertLabel.setText("⚠️ WARNING: Approaching 85% of Budget Limit!");
            budgetAlertLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-weight: bold;");
        } else {
            budgetAlertLabel.setText("✅ Budget Standing Safe");
            budgetAlertLabel.setStyle("-fx-text-fill: #10B981;");
        }
    }

    private void filterExpenses(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            expenseTable.setItems(masterExpenseList);
            return;
        }
        String cleanKeyword = keyword.toLowerCase().trim();
        ObservableList<Expense> filtered = masterExpenseList.stream()
                .filter(e -> e.getDate().toLowerCase().contains(cleanKeyword) ||
                        e.getCategoryName().toLowerCase().contains(cleanKeyword) ||
                        e.getDescription().toLowerCase().contains(cleanKeyword))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        expenseTable.setItems(filtered);
    }

    @FXML
    void openExpenseForm(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/expensetracker/expense_form.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        ExpenseController contr = loader.getController();
        contr.setParentController(this, currentUser.getId(), null);
        stage.showAndWait();
    }

    @FXML
    void handleEditExpense(ActionEvent event) throws IOException {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, expenseTable.getScene().getWindow(), "Selection Error", "Please select an expense row to update.");
            return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/expensetracker/expense_form.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        ExpenseController contr = loader.getController();
        contr.setParentController(this, currentUser.getId(), selected);
        stage.showAndWait();
    }

    @FXML
    void handleDeleteExpense(ActionEvent event) {
        Expense selected = expenseTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, expenseTable.getScene().getWindow(), "Selection Error", "Please select an expense row to delete.");
            return;
        }
        if (expenseDAO.deleteExpense(selected.getId())) {
            refreshData();
        }
    }

    @FXML
    void openIncomeModule(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/expensetracker/income_view.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        refreshData();
    }

    @FXML
    void openAnalyticsView(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/expensetracker/report_view.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        ((ReportController)loader.getController()).setUserId(currentUser.getId());
        stage.showAndWait();
    }

    @FXML
    void openSettings(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/expensetracker/settings.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(loader.load()));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.showAndWait();
        refreshData();
    }

    @FXML
    void handleLogout(ActionEvent event) throws IOException {
        currentUser = null;
        Stage stage = (Stage) expenseTable.getScene().getWindow();
        stage.setScene(new Scene(new FXMLLoader(getClass().getResource("/com/example/expensetracker/login.fxml")).load(), 380, 480));
        stage.centerOnScreen();
    }
}