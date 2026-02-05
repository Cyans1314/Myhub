package com.example.myhub.service;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: User service for user management
 * ============================================================================
 */

import com.example.myhub.bean.User;
import com.example.myhub.dao.UserDao;
import com.example.myhub.util.DBUtil;

public class UserService {
    private UserDao userDao = new UserDao();

    // User registration
    public boolean register(User user) {
        // Check if username already exists
        if (userDao.usernameExists(user.getUsername())) {
            return false;
        }

        // Check if email already exists
        if (userDao.emailExists(user.getEmail())) {
            return false;
        }

        // Encrypt password
        String encryptedPassword = DBUtil.encryptPassword(user.getPassword());
        user.setPassword(encryptedPassword);

        // Save user
        return userDao.addUser(user);
    }

    // User login
    public User login(String username, String password) {
        // Encrypt input password
        String encryptedPassword = DBUtil.encryptPassword(password);

        // Validate login
        return userDao.validateLogin(username, encryptedPassword);
    }

    // Get user by ID
    public User getUserById(int id) {
        return userDao.findById(id);
    }

    // Get user by username
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    // Check if username exists
    public boolean checkUsernameExists(String username) {
        return userDao.usernameExists(username);
    }

    // Check if email exists
    public boolean checkEmailExists(String email) {
        return userDao.emailExists(email);
    }

    // Update user information
    public boolean updateUser(User user) {
        // This can add more business logic here
        // Temporarily return true, actually needs DAO support
        return true;
    }
}