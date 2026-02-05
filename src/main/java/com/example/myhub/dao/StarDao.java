package com.example.myhub.dao;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Data Access Object for repository stars
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.bean.Star;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StarDao extends BaseDao {

    // Add Star
    public boolean addStar(Star star) {
        String sql = "INSERT INTO stars(user_id, repo_id) VALUES(?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, star.getUserId());
            pstmt.setInt(2, star.getRepoId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Remove Star
    public boolean removeStar(int userId, int repoId) {
        String sql = "DELETE FROM stars WHERE user_id = ? AND repo_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, repoId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Check if user has starred a repository
    public boolean isStarred(int userId, int repoId) {
        String sql = "SELECT COUNT(*) FROM stars WHERE user_id = ? AND repo_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.setInt(2, repoId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return false;
    }

    // Get user's starred repository IDs
    public List<Integer> getStarredRepoIds(int userId) {
        List<Integer> repoIds = new ArrayList<>();
        String sql = "SELECT repo_id FROM stars WHERE user_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                repoIds.add(rs.getInt("repo_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repoIds;
    }

    // Get star count of a repository
    public int getStarCount(int repoId) {
        String sql = "SELECT COUNT(*) FROM stars WHERE repo_id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, repoId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return 0;
    }

    // Get list of users who starred a repository
    public List<Integer> getStargazers(int repoId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM stars WHERE repo_id = ? ORDER BY created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, repoId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                userIds.add(rs.getInt("user_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return userIds;
    }

    // ================= Profile page new methods =================

    // [Stars page] Get all repository details starred by a user (with author names)
    public List<Repo> findStarredReposByUser(int userId) {
        List<Repo> repos = new ArrayList<>();
        // Key: Stars table JOIN Repositories table JOIN Users table
        String sql = "SELECT r.*, u.username as owner_name FROM repositories r " +
                "JOIN stars s ON r.id = s.repo_id " +
                "JOIN users u ON r.owner_id = u.id " +
                "WHERE s.user_id = ? " +
                "ORDER BY s.created_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Repo repo = new Repo();
                repo.setId(rs.getInt("id"));
                repo.setName(rs.getString("name"));
                repo.setDescription(rs.getString("description"));
                repo.setOwnerId(rs.getInt("owner_id"));
                repo.setStarCount(rs.getInt("star_count"));
                repo.setPublic(rs.getBoolean("is_public"));
                repo.setUpdatedAt(rs.getTimestamp("updated_at"));

                // This is an extended field, make sure you added ownerName to Repo.java
                repo.setOwnerName(rs.getString("owner_name"));

                repos.add(repo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }
}