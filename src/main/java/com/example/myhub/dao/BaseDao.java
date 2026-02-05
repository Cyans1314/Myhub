package com.example.myhub.dao;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Base Data Access Object class providing common database operations
 * ============================================================================
 */

import com.example.myhub.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BaseDao {

    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    protected void closeResources(Connection conn, PreparedStatement pstmt, ResultSet rs) {
        DBUtil.close(conn, pstmt, rs);
    }

    protected void closeResources(Connection conn, PreparedStatement pstmt) {
        DBUtil.close(conn, pstmt);
    }
}