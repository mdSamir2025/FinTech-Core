package com.example.expensetracker.dao;

import com.example.expensetracker.model.User;
import com.example.expensetracker.util.DBConnection;
import java.sql.*;

public class UserDAO {
    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, Integer.toHexString(password.hashCode()));
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public User loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, Integer.toHexString(password.hashCode()));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("username"), rs.getString("password"), rs.getDouble("monthly_limit"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateMonthlyLimit(int userId, double limit) {
        String sql = "UPDATE users SET monthly_limit = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, limit);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String oldPass, String newPass) {
        String checkSql = "SELECT password FROM users WHERE id = ?";
        String updateSql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
                pstmt.setInt(1, userId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next() && rs.getString("password").equals(Integer.toHexString(oldPass.hashCode()))) {
                        try (PreparedStatement upstmt = conn.prepareStatement(updateSql)) {
                            upstmt.setString(1, Integer.toHexString(newPass.hashCode()));
                            upstmt.setInt(2, userId);
                            return upstmt.executeUpdate() > 0;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}