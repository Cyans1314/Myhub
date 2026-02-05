-- ============================================================================
-- Author: Cyans
-- From: Chang'an University
-- Date: 2025-12-23
-- Description: MyHub Database Initialization Script
-- ============================================================================

-- Drop and create database
DROP DATABASE IF EXISTS myhub;
CREATE DATABASE myhub CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE myhub;

-- Create users table
CREATE TABLE users
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    avatar     VARCHAR(255) DEFAULT NULL,
    bio        TEXT,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
);

-- Create repositories table
CREATE TABLE repositories
(
    id             INT PRIMARY KEY AUTO_INCREMENT,
    name           VARCHAR(100) NOT NULL,
    description    VARCHAR(500) DEFAULT NULL,
    owner_id       INT          NOT NULL,
    is_public      TINYINT(1)   DEFAULT 1,
    default_branch VARCHAR(100) DEFAULT 'main',
    star_count     INT          DEFAULT 0,
    created_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_owner_repo (owner_id, name),
    FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_name (name),
    INDEX idx_owner (owner_id)
);

-- Create issues table
CREATE TABLE issues
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    repo_id    INT          NOT NULL,
    user_id    INT          NOT NULL,
    title      VARCHAR(200) NOT NULL,
    content    TEXT         NOT NULL,
    status     ENUM ('OPEN', 'CLOSED') DEFAULT 'OPEN',  -- Fix: Removed 'REOPENED'
    created_at TIMESTAMP                           DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP                           DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (repo_id) REFERENCES repositories (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_repo_status (repo_id, status),
    INDEX idx_user (user_id)
);

-- Create issue comments table
CREATE TABLE issue_comments
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    issue_id   INT  NOT NULL,
    user_id    INT  NOT NULL,
    content    TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (issue_id) REFERENCES issues (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_issue (issue_id),
    INDEX idx_user (user_id)
);

-- Create stars table
CREATE TABLE stars
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    user_id    INT NOT NULL,
    repo_id    INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_repo (user_id, repo_id),
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (repo_id) REFERENCES repositories (id) ON DELETE CASCADE,
    INDEX idx_user (user_id),
    INDEX idx_repo (repo_id)
);

-- Create branches table
CREATE TABLE branches
(
    id         INT PRIMARY KEY AUTO_INCREMENT,
    repo_id    INT          NOT NULL,
    name       VARCHAR(100) NOT NULL,
    is_default TINYINT(1) DEFAULT 0,
    created_at TIMESTAMP  DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (repo_id) REFERENCES repositories (id) ON DELETE CASCADE,
    UNIQUE KEY uk_repo_branch (repo_id, name),
    INDEX idx_repo (repo_id)
);

-- Insert test data

-- Insert users (password is MD5 hash of 123456)
INSERT INTO users (username, email, password, bio)
VALUES ('admin', 'admin@myhub.com', 'e10adc3949ba59abbe56e057f20f883e', '管理员'),
       ('user1', 'user1@myhub.com', 'e10adc3949ba59abbe56e057f20f883e', '用户1');

-- Insert repositories
INSERT INTO repositories (name, description, owner_id)
VALUES ('hello-world', '我的第一个仓库', 1),
       ('my-project', 'Java项目', 1),
       ('demo', '演示项目', 2);

-- Insert issues
INSERT INTO issues (repo_id, user_id, title, content, status)
VALUES (1, 1, '欢迎使用', '这是一个测试Issue', 'OPEN'),
       (1, 2, '发现bug', '这里有一个问题', 'OPEN'),
       (2, 2, '项目结构', '建议优化项目结构', 'CLOSED');

-- Insert comments
INSERT INTO issue_comments (issue_id, user_id, content)
VALUES (1, 2, '欢迎！'),
       (1, 1, '谢谢！'),
       (3, 1, '已处理');

-- Insert star records
INSERT INTO stars (user_id, repo_id)
VALUES (2, 1),
       (1, 3);

-- Update star count
UPDATE repositories
SET star_count = 1
WHERE id = 1;
UPDATE repositories
SET star_count = 1
WHERE id = 3;

-- Insert branch data
INSERT INTO branches (repo_id, name, is_default)
VALUES (1, 'main', 1),
       (1, 'dev', 0),
       (2, 'master', 1),
       (3, 'main', 1);

-- Display all data
SELECT 'Users Table:' AS '';
SELECT id, username, email
FROM users;

SELECT 'Repositories Table:' AS '';
SELECT id, name, description, owner_id, star_count
FROM repositories;

SELECT 'Issues Table:' AS '';
SELECT id, repo_id, title, status
FROM issues;

SELECT 'Comments Table:' AS '';
SELECT id, issue_id, user_id, LEFT(content, 20) AS content_preview
FROM issue_comments;

SELECT 'Stars Table:' AS '';
SELECT id, user_id, repo_id
FROM stars;

SELECT 'Branches Table:' AS '';
SELECT id, repo_id, name, is_default
FROM branches;