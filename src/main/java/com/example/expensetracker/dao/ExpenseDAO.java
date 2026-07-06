package com.example.expensetracker.dao;

import com.example.expensetracker.model.Expense;
import com.example.expensetracker.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDAO {
    public boolean addExpense(int userId, int catId, double amount, String date, String desc) {
        String sql = "INSERT INTO expenses (user_id, category_id, amount, date, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, catId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, date);
            pstmt.setString(5, desc);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateExpense(int id, int catId, double amount, String date, String desc) {
        String sql = "UPDATE expenses SET category_id = ?, amount = ?, date = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, catId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            pstmt.setString(4, desc);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteExpense(int id) {
        String sql = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Expense> getExpensesByUser(int userId) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT e.*, c.name AS category_name FROM expenses e JOIN categories c ON e.category_id = c.id WHERE e.user_id = ? ORDER BY e.date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Expense exp = new Expense(rs.getInt("id"), rs.getInt("user_id"), rs.getInt("category_id"), rs.getDouble("amount"), rs.getString("date"), rs.getString("description"));
                    exp.setCategoryName(rs.getString("category_name"));
                    expenses.add(exp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return expenses;
    }

    public Map<String, Double> getExpenseSummaryByCategory(int userId) {
        Map<String, Double> map = new HashMap<>();
        String sql = "SELECT c.name, SUM(e.amount) AS total FROM expenses e JOIN categories c ON e.category_id = c.id WHERE e.user_id = ? GROUP BY c.name";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("name"), rs.getDouble("total"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void wipeData(int userId) {
        String sql1 = "DELETE FROM expenses WHERE user_id = ?";
        String sql2 = "DELETE FROM income WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement p1 = conn.prepareStatement(sql1)) { p1.setInt(1, userId); p1.executeUpdate(); }
            try (PreparedStatement p2 = conn.prepareStatement(sql2)) { p2.setInt(1, userId); p2.executeUpdate(); }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}