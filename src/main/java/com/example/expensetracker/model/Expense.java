package com.example.expensetracker.model;

public class Expense {
    private int id;
    private int userId;
    private int categoryId;
    private double amount;
    private String date;
    private String description;
    private String categoryName;

    public Expense(int id, int userId, int categoryId, double amount, String date, String description) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getCategoryId() { return categoryId; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String name) { this.categoryName = name; }
}