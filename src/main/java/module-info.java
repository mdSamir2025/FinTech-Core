module com.example.expensetracker {
    // JavaFX modules
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    // JDBC for MySQL
    requires java.sql;

    // Export your packages so FXML can access controllers
    opens com.example.expensetracker.controller to javafx.fxml;
    opens com.example.expensetracker.model to javafx.base;

    // Export base package for Main.java
    exports com.example.expensetracker;
    exports com.example.expensetracker.controller;
    exports com.example.expensetracker.model;
}
