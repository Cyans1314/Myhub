package com.example.myhub.util;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Database utility for connection management
 * ============================================================================
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    static {
        try {
            // Force print driver loading information
            System.out.println("========== Loading database driver... ==========");
            String driver = Config.get("db.driver");
            System.out.println("Driver class name: " + driver);
            Class.forName(driver);
            System.out.println("========== Database driver loaded successfully! ==========");
        } catch (ClassNotFoundException e) {
            System.err.println("!!!!!!!!!! Database driver not found !!!!!!!!!!!");
            e.printStackTrace(); // Force print
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            String url = Config.get("db.url");
            String username = Config.get("db.username");
            String password = Config.get("db.password");

            // Print connection information (Note: don't screenshot the password and send to others, just check it yourself)
            System.out.println("========== Attempting to connect to database ==========");
            System.out.println("URL: " + url);
            System.out.println("User: " + username);

            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("========== Database connection successful!!! ==========");
            return conn;
        } catch (SQLException e) {
            System.err.println("!!!!!!!!!! Database connection completely failed !!!!!!!!!!!");
            System.err.println("Error reason: " + e.getMessage());
            e.printStackTrace(); // Force print
            throw e;
        }
    }

    public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void close(Connection conn, PreparedStatement pstmt) {
        close(conn, pstmt, null);
    }

    // MD5 encryption
    public static String encryptPassword(String password) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    public static boolean verifyPassword(String input, String encrypted) {
        return encryptPassword(input).equals(encrypted);
    }
}