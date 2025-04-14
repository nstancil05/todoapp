package com.todoapp;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class LoginScreen {

    private final UserManager userManager;

    public LoginScreen() {
        userManager = new UserManager();
    }

    public void show(Stage stage, Consumer<User> onLoginSuccess) {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginButton = new Button("Login");
        Button registerButton = new Button("Register");

        Label statusLabel = new Label();

        VBox loginLayout = new VBox(10, usernameField, passwordField, loginButton, registerButton, statusLabel);
        loginLayout.setPadding(new Insets(20));
        Scene loginScene = new Scene(loginLayout, 300, 200);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            User user = userManager.loginUser(username, password);
            if (user != null) {
                System.out.println("✅ Login successful as " + user.getUsername());
                onLoginSuccess.accept(user);
            } else {
                statusLabel.setText("❌ Invalid credentials.");
            }
        });

        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            if (userManager.registerUser(username, password)) {
                statusLabel.setText("✅ Registration successful. Please log in.");
            } else {
                statusLabel.setText("❌ Username already exists.");
            }
        });

        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }
}