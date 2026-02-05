package com.example.myhub.dao;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Data Access Object for repositories
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepoDao extends BaseDao {

    private static final Logger logger = LoggerFactory.getLogger(RepoDao.class);

    // Create repository
    public boolean addRepo(Repo repo) {
        // Modification 1: Add is_public to SQL statement
        String sql = "INSERT INTO repositories(name, description, owner_id, default_branch, is_public) VALUES(?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, repo.getName());
            pstmt.setString(2, repo.getDescription());
            pstmt.setInt(3, repo.getOwnerId());
            pstmt.setString(4, repo.getDefaultBranch());


            pstmt.setInt(5, repo.isPublic() ? 1 : 0);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to add repository: " + repo.getName(), e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Query repository by ID
    public Repo findById(int id) {
        String sql = "SELECT * FROM repositories WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return createRepoFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Failed to query repository by ID: " + id, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    // Query repository by owner ID and repository name
    public Repo findByOwnerAndName(int ownerId, String repoName) {
        String sql = "SELECT * FROM repositories WHERE owner_id = ? AND name = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ownerId);
            pstmt.setString(2, repoName);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return createRepoFromResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("Failed to query repository {} for user {}", repoName, ownerId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return null;
    }

    // Get all repositories of a user
    public List<Repo> findByOwnerId(int ownerId) {
        List<Repo> repos = new ArrayList<>();
        String sql = "SELECT * FROM repositories WHERE owner_id = ? ORDER BY updated_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ownerId);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                repos.add(createRepoFromResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("Failed to query repositories for user {}", ownerId, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }

    // Get all public repositories
    // Get all public repositories (with username)
    public List<Repo> findPublicRepos() {
        List<Repo> repos = new ArrayList<>();
        // Join query (JOIN users) to get username
        String sql = "SELECT r.*, u.username FROM repositories r " +
                "JOIN users u ON r.owner_id = u.id " +
                "WHERE r.is_public = 1 ORDER BY r.updated_at DESC";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Repo repo = createRepoFromResultSet(rs);
                // Manually set ownerName
                repo.setOwnerName(rs.getString("username"));
                repos.add(repo);
            }
        } catch (SQLException e) {
            // e.printStackTrace(); // Remember to handle logging
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }

    // Search repositories (by name)
    public List<Repo> searchByName(String keyword) {
        List<Repo> repos = new ArrayList<>();
        String sql = "SELECT r.*, u.username as owner_name FROM repositories r " +
                "JOIN users u ON r.owner_id = u.id " +
                "WHERE r.name LIKE ? AND r.is_public = 1 ORDER BY r.updated_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Repo repo = createRepoFromResultSet(rs);
                repo.setOwnerName(rs.getString("owner_name"));
                repos.add(repo);
            }
        } catch (SQLException e) {
            logger.error("Failed to search repositories with keyword: " + keyword, e);
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }

    // Update star count
    public boolean updateStarCount(int repoId, int count) {
        String sql = "UPDATE repositories SET star_count = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, count);
            pstmt.setInt(2, repoId);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update star count for repository {}", repoId, e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Delete repository
    public boolean deleteRepo(int id) {
        String sql = "DELETE FROM repositories WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to delete repository {}", id, e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Check if repository exists
    public boolean exists(int ownerId, String repoName) {
        return findByOwnerAndName(ownerId, repoName) != null;
    }

    // Create Repo object from ResultSet
    private Repo createRepoFromResultSet(ResultSet rs) throws SQLException {
        Repo repo = new Repo();
        repo.setId(rs.getInt("id"));
        repo.setName(rs.getString("name"));
        repo.setDescription(rs.getString("description"));
        repo.setOwnerId(rs.getInt("owner_id"));
        repo.setPublic(rs.getBoolean("is_public"));
        repo.setDefaultBranch(rs.getString("default_branch"));
        repo.setStarCount(rs.getInt("star_count"));
        repo.setCreatedAt(rs.getTimestamp("created_at"));
        repo.setUpdatedAt(rs.getTimestamp("updated_at"));
        return repo;
    }

    // ================= Profile page new methods =================

    // 1. [Overview page] Get user's 6 most popular public repositories
    public List<Repo> findPopularRepos(int userId) {
        List<Repo> repos = new ArrayList<>();
        String sql = "SELECT * FROM repositories WHERE owner_id = ? AND is_public = 1 ORDER BY star_count DESC LIMIT 6";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                repos.add(createRepoFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }

    // 2. [Repo list page] Get all **public** repositories of a user (used when viewing others' profiles)
    public List<Repo> findPublicByOwnerId(int ownerId) {
        List<Repo> repos = new ArrayList<>();
        String sql = "SELECT * FROM repositories WHERE owner_id = ? AND is_public = 1 ORDER BY updated_at DESC";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, ownerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                repos.add(createRepoFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return repos;
    }

    // 3. [Contribution graph] Count repository creation count per day for the past year
    public java.util.Map<String, Integer> getContributionData(int userId) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        // 统计 created_at (按天分组)
        String sql = "SELECT DATE_FORMAT(created_at, '%Y-%m-%d') as date_str, COUNT(*) as cnt " +
                "FROM repositories " +
                "WHERE owner_id = ? AND created_at > DATE_SUB(NOW(), INTERVAL 1 YEAR) " +
                "GROUP BY date_str";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                map.put(rs.getString("date_str"), rs.getInt("cnt"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, pstmt, rs);
        }
        return map;
    }

    // ================= Settings page new methods =================

    // Update repository name
    public boolean updateName(int repoId, String newName) {
        String sql = "UPDATE repositories SET name = ?, updated_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newName);
            pstmt.setInt(2, repoId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update repository name: repoId={}, newName={}", repoId, newName, e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Update repository description
    public boolean updateDescription(int repoId, String description) {
        String sql = "UPDATE repositories SET description = ?, updated_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, description);
            pstmt.setInt(2, repoId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update repository description: repoId={}", repoId, e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }

    // Update repository visibility
    public boolean updateVisibility(int repoId, boolean isPublic) {
        String sql = "UPDATE repositories SET is_public = ?, updated_at = NOW() WHERE id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, isPublic ? 1 : 0);
            pstmt.setInt(2, repoId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Failed to update repository visibility: repoId={}, isPublic={}", repoId, isPublic, e);
            return false;
        } finally {
            closeResources(conn, pstmt);
        }
    }
}
