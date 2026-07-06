package com.example.expensetracker.dao;

import com.example.expensetracker.model.Income;
import com.example.expensetracker.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncomeDAO {
    public boolean addIncome(int userId, double amount, String date, String source) {
        String sql = "INSERT INTO income (user_id, amount, date, source) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, date);
            pstmt.setString(4, source);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updateIncome(int id, double amount, String date, String source) {
        String sql = "UPDATE income SET amount = ?, date = ?, source = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, date);
            pstmt.setString(3, source);
            pstmt.setInt(4, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteIncome(int id) {
        String sql = "DELETE FROM income WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Income> getIncomeByUser(int userId) {
        List<Income> incomes = new ArrayList<>();
        String sql = "SELECT * FROM income WHERE user_id = ? ORDER BY date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    incomes.add(new Income(rs.getInt("id"), rs.getInt("user_id"), rs.getDouble("amount"), rs.getString("date"), rs.getString("source")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return incomes;
    }
}