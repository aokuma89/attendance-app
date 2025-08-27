package com.example.attendance.dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.attendance.dto.User;

public class UserDAO {

    private static final Map<String, User> users = new HashMap<>();

    static {
        // 初期ユーザー
        users.put("employee1", new User("employee1", hashPassword("password"), "employee", true));
        users.put("admin1", new User("admin1", hashPassword("adminpass"), "admin", true));
        users.put("employee2", new User("employee2", hashPassword("password"), "employee", true));
    }

    public User findByUsername(String username) {
        return users.get(username);
    }
    
    public boolean verifyPassword(String username, String password) { 
        User user = findByUsername(username); 
        return user != null && user.isEnabled() && user.getPassword().equals(hashPassword(password)); 
    } 

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    public void updateUser(User user) {
        User existing = users.get(user.getUsername());
        if (existing != null) {
            existing.setEnabled(user.isEnabled());
            existing.setRole(user.getRole());
        }
    }

    public void deleteUser(String username) {
        users.remove(username);
    }

    public void resetPassword(String username, String newPassword) {
        User user = users.get(username);
        if (user != null) {
            try {
                java.lang.reflect.Field passwordField = User.class.getDeclaredField("password");
                passwordField.setAccessible(true);
                passwordField.set(user, hashPassword(newPassword));
            } catch (Exception e) {
                throw new RuntimeException("パスワード更新失敗", e);
            }
        }
    }

    public void toggleUserEnabled(String username, boolean enabled) {
        User user = users.get(username);
        if (user != null) {
            user.setEnabled(enabled);
        }
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
