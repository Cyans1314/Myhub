package com.example.myhub.dto;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Data Transfer Object for file node information
 * ============================================================================
 */

public class FileNode {
    private String name;
    private String type;  // "file" or "directory"
    private String path;
    private long size;
    private String lastCommit;
    private String lastCommitTime;

    public FileNode() {}

    public FileNode(String name, String type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public long getSize() { return size; }
    public void setSize(long size) { this.size = size; }

    public String getLastCommit() { return lastCommit; }
    public void setLastCommit(String lastCommit) { this.lastCommit = lastCommit; }

    public String getLastCommitTime() { return lastCommitTime; }
    public void setLastCommitTime(String lastCommitTime) { this.lastCommitTime = lastCommitTime; }
}