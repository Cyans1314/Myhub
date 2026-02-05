package com.example.myhub.bean;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Issue entity class for repository issues
 * ============================================================================
 */

import java.util.Date;

public class Issue {
    private int id;
    private int repoId;
    private int userId;
    private String title;
    private String content;
    private String status;  // OPEN, CLOSED
    private Date createdAt;
    private Date updatedAt;

    // Additional fields for display purposes
    private String username;
    private int commentCount;

    public Issue() {}

    public Issue(int repoId, int userId, String title, String content) {
        this.repoId = repoId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.status = "OPEN";
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRepoId() { return repoId; }
    public void setRepoId(int repoId) { this.repoId = repoId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
}