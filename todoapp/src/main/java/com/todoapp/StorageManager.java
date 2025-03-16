package com.todoapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StorageManager {

    private static final String FILE_PATH = "data/tasks.json"; // Store in 'data' folder
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
            .create();

    /**
     * Save the list of tasks to a JSON file.
     *
     * @param tasks List of Task objects to be saved.
     */
    public static void saveTasks(List<Task> tasks) {
        try {
            // Ensure 'data' directory exists
            File dataDirectory = new File("data");
            if (!dataDirectory.exists()) {
                boolean created = dataDirectory.mkdirs();
                if (created) {
                    System.out.println("✅ 'data' directory created.");
                } else {
                    System.err.println("❌ Failed to create 'data' directory.");
                }
            }

            // Write tasks to JSON file
            try (FileWriter writer = new FileWriter(FILE_PATH)) {
                gson.toJson(tasks, writer);
                System.out.println("✅ Tasks saved successfully to " + FILE_PATH);
            }
        } catch (IOException e) {
            System.err.println("❌ Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Load the list of tasks from a JSON file.
     *
     * @return List of Task objects, or an empty list if file doesn't exist or error occurs.
     */
    public static List<Task> loadTasks() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type taskListType = new TypeToken<List<Task>>() {}.getType();
            List<Task> tasks = gson.fromJson(reader, taskListType);
            if (tasks != null) {
                System.out.println("✅ Tasks loaded successfully from " + FILE_PATH);
                return tasks;
            } else {
                System.out.println("⚠️ No tasks found, returning empty list.");
                return new ArrayList<>();
            }
        } catch (IOException e) {
            System.err.println("⚠️ Error loading tasks (file may not exist yet): " + e.getMessage());
            return new ArrayList<>();
        }
    }
}