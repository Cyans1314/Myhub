package com.example.myhub.dto;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Data Transfer Object for commit information
 * ============================================================================
 */

import java.util.Date;

public class CommitInfo {
    private String hash;
    private String shortHash;
    private String author;
    private String email;
    private Date time;
    private String message;

    public CommitInfo() {}

    // Getters and Setters
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }

    public String getShortHash() { return shortHash; }
    public void setShortHash(String shortHash) { this.shortHash = shortHash; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}