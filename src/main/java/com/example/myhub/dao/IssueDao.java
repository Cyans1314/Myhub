package com.example.myhub.dao;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Data Access Object for repository issues
 * ============================================================================
 */

import com.example.myhub.bean.Issue;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueDao extends BaseDao {

    // Add Issue (returns generated ID)
    public boolean insert(Issue issue) {
        String sql = "INSERT INTO issues(repo_id, user_id, title, content, status, created_at, updated_at) VALUES(?, ?, ?, ?, ?, NOW(), NOW())";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, issue.getRepoId());
            pstmt.setInt(2, issue.getUserId());
            pstmt.setString(3, issue.getTitle());
            pstmt.setString(4, issue.getContent());
            pstmt.setString(5, issue.getStatus());

            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    issue.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Add Issue (old method, kept for compatibility)
    public boolean addIssue(Issue issue) {
        return insert(issue);
    }

    // Query Issue by ID
    public Issue findById(int id) {
        String sql = "SELECT * FROM issues WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return createIssueFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    // Get all Issues of a repository (filter by status)
    public List<Issue> findByRepoAndStatus(int repoId, String status) {
        return findByRepoId(repoId, status);
    }

    // Get all Issues of a repository
    public List<Issue> findByRepoId(int repoId, String status) {
        List<Issue> issues = new ArrayList<>();
        String sql;

        if (status == null || status.isEmpty()) {
            sql = "SELECT * FROM issues WHERE repo_id = ? ORDER BY updated_at DESC";
        } else {
            sql = "SELECT * FROM issues WHERE repo_id = ? AND status = ? ORDER BY updated_at DESC";
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, repoId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(2, status);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                issues.add(createIssueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return issues;
    }

    // Get all Issues of a user
    public List<Issue> findByUserId(int userId) {
        List<Issue> issues = new ArrayList<>();
        String sql = "SELECT * FROM issues WHERE user_id = ? ORDER BY updated_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                issues.add(createIssueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return issues;
    }

    // Update Issue status
    public boolean updateStatus(int issueId, String status) {
        String sql = "UPDATE issues SET status = ?, updated_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, issueId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Update Issue content
    public boolean updateIssue(int issueId, String title, String content) {
        String sql = "UPDATE issues SET title = ?, content = ?, updated_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, title);
            pstmt.setString(2, content);
            pstmt.setInt(3, issueId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Delete Issue
    public boolean deleteIssue(int id) {
        String sql = "DELETE FROM issues WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Count Issues of a repository (by status)
    public int countByRepoAndStatus(int repoId, String status) {
        return countByRepo(repoId, status);
    }

    // Count Issues of a repository
    public int countByRepo(int repoId, String status) {
        String sql;
        if (status == null || status.isEmpty()) {
            sql = "SELECT COUNT(*) FROM issues WHERE repo_id = ?";
        } else {
            sql = "SELECT COUNT(*) FROM issues WHERE repo_id = ? AND status = ?";
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, repoId);

            if (status != null && !status.isEmpty()) {
                pstmt.setString(2, status);
            }

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

    // Create Issue object from ResultSet
    private Issue createIssueFromResultSet(ResultSet rs) throws SQLException {
        Issue issue = new Issue();
        issue.setId(rs.getInt("id"));
        issue.setRepoId(rs.getInt("repo_id"));
        issue.setUserId(rs.getInt("user_id"));
        issue.setTitle(rs.getString("title"));
        issue.setContent(rs.getString("content"));
        issue.setStatus(rs.getString("status"));
        issue.setCreatedAt(rs.getTimestamp("created_at"));
        issue.setUpdatedAt(rs.getTimestamp("updated_at"));
        return issue;
    }
}