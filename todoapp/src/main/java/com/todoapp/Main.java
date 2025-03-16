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

    private List<Task> tasks;
    private Task taskBeingEdited = null; // Track the task being edited

    @Override
    public void start(Stage primaryStage) {
        tasks = StorageManager.loadTasks(); // Load tasks

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

        // === Sorting Controls ===
        ChoiceBox<String> sortChoiceBox = new ChoiceBox<>();
        sortChoiceBox.getItems().addAll("Due Date", "Priority", "Priority & Due Date", "Completion Status");
        sortChoiceBox.setValue("Due Date");

        CheckBox reverseSortBox = new CheckBox("Reverse Order");

        Button applySortButton = new Button("Sort Tasks");

        // === Add or Save Changes Button Action ===
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
                System.out.println("✅ Task updated!");
                taskBeingEdited = null; // Clear editing mode
                addButton.setText("Add Task"); // Reset button text when done editing
            } else {
                Task task = new Task(name, description, dueDateConverted, priority, category);
                tasks.add(task);
            }

            StorageManager.saveTasks(tasks);
            refreshTaskList(taskListView);

            nameField.clear();
            descriptionArea.clear();
            dueDatePicker.setValue(null);
            categoryChoiceBox.setValue(null);
            priorityChoiceBox.setValue(null);
        });

        // === Edit Button Action ===
        editButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                nameField.setText(selectedTask.getName());
                descriptionArea.setText(selectedTask.getDescription());
                dueDatePicker.setValue(selectedTask.getDueDate() != null ? ((java.sql.Date) selectedTask.getDueDate()).toLocalDate() : null);
                categoryChoiceBox.setValue(selectedTask.getCategory());
                priorityChoiceBox.setValue(getPriorityLabel(selectedTask.getPriority()));
                taskBeingEdited = selectedTask;
                addButton.setText("Save Changes"); // Set button text to Save Changes when editing
            }
        });

        // === View Details Button Action ===
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

        // === Toggle Completed on Click ===
        taskListView.setOnMouseClicked(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                selectedTask.toggleCompleted();
                StorageManager.saveTasks(tasks);
                refreshTaskList(taskListView);
            }
        });

        // === Delete Selected Task ===
        deleteButton.setOnAction(e -> {
            Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
            if (selectedTask != null) {
                tasks.remove(selectedTask);
                StorageManager.saveTasks(tasks);
                refreshTaskList(taskListView);
            }
        });

        // === Apply Sorting ===
        applySortButton.setOnAction(e -> {
            String selectedSort = sortChoiceBox.getValue();
            Comparator<Task> comparator = switch (selectedSort) {
                case "Priority" -> Comparator.comparingInt(Task::getPriority);
                case "Priority & Due Date" -> Comparator.comparingInt(Task::getPriority)
                        .thenComparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
                case "Completion Status" -> Comparator.comparing(Task::isCompleted)
                        .thenComparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
                default -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
            };
            if (reverseSortBox.isSelected()) comparator = comparator.reversed();
            tasks.sort(comparator);
            refreshTaskList(taskListView);
        });

        VBox layout = new VBox(10);
        HBox buttonRow = new HBox(10, deleteButton, editButton, viewDetailsButton);
        HBox sortRow = new HBox(10, new Label("Sort By:"), sortChoiceBox, reverseSortBox, applySortButton);

        layout.getChildren().addAll(
                nameField, descriptionArea, dueDatePicker, categoryChoiceBox, priorityChoiceBox, addButton, buttonRow, sortRow, taskListView
        );

        refreshTaskList(taskListView);

        Scene scene = new Scene(layout, 500, 600);
        primaryStage.setTitle("To-Do List Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private int mapPriority(String priorityStr) { return switch (priorityStr) { case "High" -> 1; case "Medium" -> 2; case "Low" -> 3; default -> 3; }; }
    private String getPriorityLabel(int priority) { return switch (priority) { case 1 -> "High"; case 2 -> "Medium"; case 3 -> "Low"; default -> "Low"; }; }
    private void refreshTaskList(ListView<Task> listView) { listView.getItems().setAll(tasks); if (!listView.getItems().isEmpty() && listView.getSelectionModel().getSelectedItem() == null) listView.getSelectionModel().selectFirst(); }

    public static void main(String[] args) { launch(args); }
}