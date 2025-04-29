package com.todoapp;

import java.util.*;
import java.time.LocalDate;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private TaskManager taskManager;
    private Task taskBeingEdited = null; // Track the task being edited

    @Override
    public void start(Stage primaryStage) {
        LoginScreen loginScreen = new LoginScreen();
        loginScreen.show(primaryStage, user -> launchMainApp(primaryStage, user));
    }

    public void launchMainApp(Stage primaryStage, User user) {
        taskManager = new TaskManager(user); // Load tasks specific to the logged-in user

        ListView<Task> taskListView = new ListView<>();

        // === Form Fields ===
        TextField nameField = new TextField();
        nameField.setPromptText("Task Name");

        TextArea descriptionArea = new TextArea();
        descriptionArea.setPromptText("Task Description");

        DatePicker dueDatePicker = new DatePicker();
        dueDatePicker.setPromptText("Due Date");

        ChoiceBox<Category> categoryChoiceBox = new ChoiceBox<>();
        categoryChoiceBox.getItems().addAll(Category.values());

        ChoiceBox<String> priorityChoiceBox = new ChoiceBox<>();
        priorityChoiceBox.getItems().addAll("High", "Medium", "Low");

        Button addButton = new Button("Add Task");
        Button deleteButton = new Button("Delete Task");
        Button editButton = new Button("Edit Task");
        Button viewDetailsButton = new Button("View Details");
        Button logoutButton = new Button("Logout");

        // === Logout ===
        logoutButton.setOnAction(e -> {
            taskManager.saveTasks();         // Save any task changes
            start(primaryStage);             // Return to login screen
        });

        // === Theme Toggle ===
        CheckBox darkModeToggle = new CheckBox("🌙 Dark Mode");

        // === Sorting Controls ===
        ChoiceBox<String> sortChoiceBox = new ChoiceBox<>();
        sortChoiceBox.getItems().addAll("Due Date", "Priority", "Priority & Due Date", "Completion Status");
        sortChoiceBox.setValue("Due Date");

        CheckBox reverseSortBox = new CheckBox("Reverse Order");
        Button applySortButton = new Button("Sort Tasks");

        // === Add or Save Task ===
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String description = descriptionArea.getText();
            LocalDate dueDate = dueDatePicker.getValue();
            Category category = categoryChoiceBox.getValue();
            String priorityStr = priorityChoiceBox.getValue();
            int priority = mapPriority(priorityStr);

            Date dueDateConverted = (dueDate != null) ? java.sql.Date.valueOf(dueDate) : null;

            if (name.isEmpty() || category == null || priorityStr == null) {
                System.out.println("⚠️ Please fill in at least name, category, and priority.");
                return;
            }

            if (taskBeingEdited != null) {
                taskBeingEdited.editTask(name, description, dueDateConverted, priority, category);
                taskBeingEdited = null;
                addButton.setText("Add Task");
                System.out.println("✅ Task updated!");
            } else {
                Task task = new Task(name, description, dueDateConverted, priority, category);
                taskManager.addTask(task);
            }

            taskManager.saveTasks();
            refreshTaskList(taskListView);

            nameField.clear();
            descriptionArea.clear();
            dueDatePicker.setValue(null);
            categoryChoiceBox.setValue(null);
            priorityChoiceBox.setValue(null);
        });

        editButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                nameField.setText(selectedTask.getName());
                descriptionArea.setText(selectedTask.getDescription());
                dueDatePicker.setValue(selectedTask.getDueDate() != null ? ((java.sql.Date) selectedTask.getDueDate()).toLocalDate() : null);
                categoryChoiceBox.setValue(selectedTask.getCategory());
                priorityChoiceBox.setValue(getPriorityLabel(selectedTask.getPriority()));
                taskBeingEdited = selectedTask;
                addButton.setText("Save Changes");
            }
        });

        viewDetailsButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Task Details");
                alert.setHeaderText("Details for: " + selectedTask.getName());
                String dueDateStr = (selectedTask.getDueDate() != null) ? selectedTask.getDueDate().toString() : "No due date";
                String priorityStr = getPriorityLabel(selectedTask.getPriority());
                String statusStr = selectedTask.isCompleted() ? "✅ Completed" : "❌ Pending";
                String content = "Description: " + selectedTask.getDescription() + "\n" +
                        "Due Date: " + dueDateStr + "\n" +
                        "Priority: " + priorityStr + "\n" +
                        "Category: " + selectedTask.getCategory() + "\n" +
                        "Status: " + statusStr;
                alert.setContentText(content);
                alert.showAndWait();
            }
        });

        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskManager.toggleTaskCompleted(selectedTask);
                refreshTaskList(taskListView);
            }
        });

        deleteButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                taskManager.deleteTask(selectedTask);
                taskManager.saveTasks();
                refreshTaskList(taskListView);
            }
        });

        applySortButton.setOnAction(e -> {
            String selectedSort = sortChoiceBox.getValue();
            List<Task> sortedTasks = taskManager.getSortedTasks(selectedSort, reverseSortBox.isSelected());
            taskListView.getItems().setAll(sortedTasks);
        });

        VBox layout = new VBox(10);
        HBox buttonRow = new HBox(10, deleteButton, editButton, viewDetailsButton);
        HBox sortRow = new HBox(10, new Label("Sort By:"), sortChoiceBox, reverseSortBox, applySortButton);
        HBox topRow = new HBox(10, logoutButton, darkModeToggle);

        layout.getChildren().add(topRow);
        layout.getChildren().addAll(
                nameField, descriptionArea, dueDatePicker, categoryChoiceBox,
                priorityChoiceBox, addButton, buttonRow, sortRow, taskListView
        );

        refreshTaskList(taskListView);

        Scene scene = new Scene(layout, 500, 600);

        // Apply default theme and include toggle
        scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        darkModeToggle.setOnAction(e -> {
            scene.getStylesheets().clear();
            String theme = darkModeToggle.isSelected() ? "/dark-theme.css" : "/light-theme.css";
            scene.getStylesheets().add(getClass().getResource(theme).toExternalForm());
        });


        primaryStage.setTitle("To-Do List Manager - " + user.getUsername());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int mapPriority(String priorityStr) {
        return switch (priorityStr) {
            case "High" -> 1;
            case "Medium" -> 2;
            case "Low" -> 3;
            default -> 3;
        };
    }

    private String getPriorityLabel(int priority) {
        return switch (priority) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Low";
        };
    }

    private void refreshTaskList(ListView<Task> listView) {
        listView.getItems().setAll(taskManager.getTasks());
        if (!listView.getItems().isEmpty() && listView.getSelectionModel().getSelectedItem() == null)
            listView.getSelectionModel().selectFirst();
    }

    public static void main(String[] args) {
        launch(args);
    }
}