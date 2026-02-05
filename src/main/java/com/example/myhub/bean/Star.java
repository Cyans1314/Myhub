package com.example.myhub.bean;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Star entity class for repository stars
 * ============================================================================
 */

import java.util.Date;

public class Star {
    private int id;
    private int userId;
    private int repoId;
    private Date createdAt;

    public Star() {}

    public Star(int userId, int repoId) {
        this.userId = userId;
        this.repoId = repoId;
        this.createdAt = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRepoId() { return repoId; }
    public void setRepoId(int repoId) { this.repoId = repoId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}