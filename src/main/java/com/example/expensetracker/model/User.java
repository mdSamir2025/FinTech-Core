package com.example.expensetracker.model;

public class User {
    private int id;
    private String username;
    private String password;
    private double monthlyLimit;

    public User(int id, String username, String password, double monthlyLimit) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.monthlyLimit = monthlyLimit;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public double getMonthlyLimit() { return monthlyLimit; }
    public void setMonthlyLimit(double limit) { this.monthlyLimit = limit; }
}