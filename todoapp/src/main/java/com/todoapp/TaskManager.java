package com.todoapp;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {

    private final User user;
    private final List<Task> tasks;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd").create();

    public TaskManager(User user) {
        this.user = user;
        this.tasks = loadTasksForUser();
    }

    private List<Task> loadTasksForUser() {
        File file = getUserFile();
        if (!file.exists()) return new ArrayList<>();

        try (FileReader reader = new FileReader(file)) {
            Type type = new TypeToken<List<Task>>() {}.getType();
            List<Task> loaded = gson.fromJson(reader, type);
            return loaded != null ? loaded : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void saveTasks() {
        try (FileWriter writer = new FileWriter(getUserFile())) {
            gson.toJson(tasks, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File getUserFile() {
        return new File("data/" + user.getUsername() + "_tasks.json");
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void addTask(Task task) {
        tasks.add(task);
        saveTasks();
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
        saveTasks();
    }

    public void updateTask(Task task) {
        saveTasks();
    }

    public void toggleTaskCompleted(Task task) {
        task.toggleCompleted();
        saveTasks();
    }

    public List<Task> getSortedTasks(String sortBy, boolean reverse) {
        Comparator<Task> comparator = switch (sortBy) {
            case "Priority" -> Comparator.comparingInt(Task::getPriority);
            case "Priority & Due Date" -> Comparator.comparingInt(Task::getPriority)
                    .thenComparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
            case "Completion Status" -> Comparator.comparing(Task::isCompleted)
                    .thenComparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
            default -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Date::compareTo));
        };
        if (reverse) comparator = comparator.reversed();
        return tasks.stream().sorted(comparator).collect(Collectors.toList());
    }
}