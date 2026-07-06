package com.example.expensetracker.controller;

import com.example.expensetracker.dao.ExpenseDAO;
import com.example.expensetracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportController {

    @FXML private PieChart reportPieChart;

    private final ExpenseDAO expenseDAO = new ExpenseDAO();
    private int userId;

    // Dashboard Controller will invoke this to seed user contextual visibility boundaries
    public void setUserId(int userId) {
        this.userId = userId;
        loadPieChartData();
    }

    private void loadPieChartData() {
        try {
            // Fetch real-time transactional logs directly targeting current user scope
            List<Expense> list = expenseDAO.getExpensesByUser(userId);

            if (list == null || list.isEmpty()) {
                reportPieChart.setData(FXCollections.observableArrayList());
                return;
            }

            // Stream pipelines to compile summary parameters map values dynamically
            Map<String, Double> categoryTotalsMap = list.stream()
                    .collect(Collectors.groupingBy(
                            Expense::getCategoryName,
                            Collectors.summingDouble(Expense::getAmount)
                    ));

            ObservableList<PieChart.Data> chartData = FXCollections.observableArrayList();

            categoryTotalsMap.forEach((category, summaryTotal) -> {
                chartData.add(new PieChart.Data(category + " ($" + String.format("%.2f", summaryTotal) + ")", summaryTotal));
            });

            reportPieChart.setData(chartData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRefreshChart(ActionEvent event) {
        loadPieChartData();
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) reportPieChart.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}