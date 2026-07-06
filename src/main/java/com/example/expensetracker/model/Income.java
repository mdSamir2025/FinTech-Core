package com.example.expensetracker.model;

public class Income {
    private int id;
    private int userId;
    private double amount;
    private String date;
    private String source;

    public Income(int id, int userId, double amount, String date, String source) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.source = source;
    }

    // Public Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public double getAmount() { return amount; } // ← The controller calls this!
    public String getDate() { return date; }
    public String getSource() { return source; }
}