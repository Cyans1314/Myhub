package com.example.myhub.service;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Repository service for repository management
 * ============================================================================
 */

import com.example.myhub.bean.Repo;
import com.example.myhub.bean.User;
import com.example.myhub.dao.RepoDao;
import com.example.myhub.util.JGitUtil;
import com.example.myhub.service.GitService;
import com.example.myhub.util.Config;

import java.util.List;

public class RepoService {
    private RepoDao repoDao = new RepoDao();

    /**
     * Create repository
     * @param repo Repository entity
     * @param owner Owner
     * @param autoInit Whether to auto-initialize (create README)
     * @return Whether successful
     */
    // Added third parameter boolean autoInit
    public boolean createRepo(Repo repo, User owner, boolean autoInit) {
        // Check if repository already exists
        if (repoDao.exists(repo.getOwnerId(), repo.getName())) {
            return false;
        }

        // Save to database
        boolean dbSuccess = repoDao.addRepo(repo);

        if (dbSuccess) {
            // Create Git bare repository (physical path)
            String repoPath = JGitUtil.getRepoPath(owner.getUsername(), repo.getName());
            boolean gitSuccess = JGitUtil.initBareRepository(repoPath);

            // [Plan B core] If auto-init is checked and bare repository created successfully
            if (gitSuccess && autoInit) {
                // Call GitService to generate README and commit
                // Make sure you've updated the createInitialCommit method in GitService.java before
                new GitService().createInitialCommit(repo.getName(), owner);
            }

            return gitSuccess;
        }
        return false;
    }

    // Get repository by ID
    public Repo getRepoById(int id) {
        return repoDao.findById(id);
    }

    // Get repository by owner and repository name
    public Repo getRepoByOwnerAndName(int ownerId, String repoName) {
        return repoDao.findByOwnerAndName(ownerId, repoName);
    }

    // Get all repositories of a user
    public List<Repo> getUserRepos(int userId) {
        return repoDao.findByOwnerId(userId);
    }

    // Get all public repositories
    public List<Repo> getPublicRepos() {
        return repoDao.findPublicRepos();
    }

    // Search repositories
    public List<Repo> searchRepos(String keyword) {
        return repoDao.searchByName(keyword);
    }

    // Delete repository
    public boolean deleteRepo(int repoId, String username, String repoName) {
        // Delete database record
        boolean dbSuccess = repoDao.deleteRepo(repoId);
        if (dbSuccess) {
            // Delete Git repository files
            return JGitUtil.deleteRepository(username, repoName);
        }
        return false;
    }

    // Check if repository exists
    public boolean repoExists(int ownerId, String repoName) {
        return repoDao.exists(ownerId, repoName);
    }

    // Get repository clone URL
    public String getCloneUrl(String username, String repoName) {
        String siteUrl = com.example.myhub.util.Config.get("site.url", "http://localhost:8080");
        return siteUrl + "/git/" + username + "/" + repoName + ".git";
    }
}