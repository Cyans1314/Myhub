package com.example.myhub.bean;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Comment entity class for issue comments
 * ============================================================================
 */

import java.util.Date;

public class Comment {
    private int id;
    private int issueId;
    private int userId;
    private String content;
    private Date createdAt;

    // Additional field for display purposes
    private String username;

    public Comment() {}

    public Comment(int issueId, int userId, String content) {
        this.issueId = issueId;
        this.userId = userId;
        this.content = content;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIssueId() { return issueId; }
    public void setIssueId(int issueId) { this.issueId = issueId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}