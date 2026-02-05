package com.example.myhub.service;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Issue service for issue management
 * ============================================================================
 */

import com.example.myhub.bean.Issue;
import com.example.myhub.bean.Comment;
import com.example.myhub.dao.IssueDao;
import com.example.myhub.dao.UserDao;

import java.util.List;

public class IssueService {
    private IssueDao issueDao = new IssueDao();
    private UserDao userDao = new UserDao();

    // Create Issue
    public boolean createIssue(Issue issue) {
        return issueDao.addIssue(issue);
    }

    // Get Issue details
    public Issue getIssueById(int id) {
        return issueDao.findById(id);
    }

    // Get repository's Issue list
    public List<Issue> getRepoIssues(int repoId, String status) {
        return issueDao.findByRepoId(repoId, status);
    }

    // Get user's Issue list
    public List<Issue> getUserIssues(int userId) {
        return issueDao.findByUserId(userId);
    }

    // Update Issue status
    public boolean updateIssueStatus(int issueId, String status) {
        return issueDao.updateStatus(issueId, status);
    }

    // Update Issue content
    public boolean updateIssue(int issueId, String title, String content) {
        return issueDao.updateIssue(issueId, title, content);
    }

    // Delete Issue
    public boolean deleteIssue(int issueId) {
        return issueDao.deleteIssue(issueId);
    }

    // Get Issue statistics
    public int getIssueCount(int repoId, String status) {
        return issueDao.countByRepo(repoId, status);
    }

    // Get Issue author username
    public String getIssueAuthorName(int issueId) {
        Issue issue = issueDao.findById(issueId);
        if (issue != null) {
            com.example.myhub.bean.User user = userDao.findById(issue.getUserId());
            return user != null ? user.getUsername() : "Unknown";
        }
        return "Unknown";
    }
}