package com.todoapp;

import com.google.gson.reflect.TypeToken;
import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class UserManager {

    private static final String FILE_PATH = "data/users.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<User> users;

    public UserManager() {
        users = loadUsers();
    }

    public boolean registerUser(String username, String password) {
        if (getUserByUsername(username) != null) return false;
        users.add(new User(username, password));
        saveUsers();
        return true;
    }

    public User loginUser(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.checkPassword(password)) {
            return user;
        }
        return null;
    }

    private User getUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElse(null);
    }

    private void saveUsers() {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private List<User> loadUsers() {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            Type listType = new TypeToken<List<User>>() {}.getType();
            List<User> loadedUsers = gson.fromJson(reader, listType);
            return loadedUsers != null ? loadedUsers : new ArrayList<>();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
}