package com.todoapp;

import java.util.Date;

public class Task {

    // === Fields ===
    private String name;
    private String description;
    private Date dueDate;
    private int priority; // 1 = High, 2 = Medium, 3 = Low
    private Category category;
    private boolean completed;

    // === Constructor ===
    public Task(String name, String description, Date dueDate, int priority, Category category) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.category = category;
        this.completed = false; // Default to not completed
    }

    // === Setters ===
    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // === Mark as Completed ===
    public void markAsCompleted() {
        this.completed = true;
    }

    // === Toggle Completed/Uncompleted ===
    public void toggleCompleted() {
        this.completed = !this.completed; // Flip the completed status
    }

    // === Edit Task (Update All Fields) ===
    public void editTask(String name, String description, Date dueDate, int priority, Category category) {
        setName(name);
        setDescription(description);
        setDueDate(dueDate);
        setPriority(priority);
        setCategory(category);
    }

    // === Getters ===
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public Category getCategory() {
        return category;
    }

    public boolean isCompleted() {
        return completed;
    }

    // === toString for Displaying Task Info in ListView ===
    @Override
    public String toString() {
        String status = completed ? "✅ Completed" : "❌ Pending";
        String dueDateStr = (dueDate != null) ? dueDate.toString() : "No due date";
        String priorityStr = switch (priority) {
            case 1 -> "High";
            case 2 -> "Medium";
            case 3 -> "Low";
            default -> "Low";
        };

        return name + " [" + category + "] "
                + "[Priority: " + priorityStr + "] "
                + "[Due: " + dueDateStr + "] "
                + status;
    }
}