package com.example.myhub.service;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: File service for file operations
 * ============================================================================
 */

import com.example.myhub.util.Config;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileService {

    // Create directory
    public boolean createDirectory(String path) {
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                return dir.mkdirs();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if file exists
    public boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    // Delete file or directory
    public boolean deleteFile(String path) {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                return file.delete();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get file size
    public long getFileSize(String path) {
        File file = new File(path);
        return file.length();
    }

    // Get file MIME type
    public String getMimeType(String filename) {
        if (filename == null) return "application/octet-stream";

        String lowerName = filename.toLowerCase();

        if (lowerName.endsWith(".html") || lowerName.endsWith(".htm")) {
            return "text/html";
        } else if (lowerName.endsWith(".css")) {
            return "text/css";
        } else if (lowerName.endsWith(".js")) {
            return "application/javascript";
        } else if (lowerName.endsWith(".json")) {
            return "application/json";
        } else if (lowerName.endsWith(".xml")) {
            return "application/xml";
        } else if (lowerName.endsWith(".txt")) {
            return "text/plain";
        } else if (lowerName.endsWith(".md")) {
            return "text/markdown";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerName.endsWith(".png")) {
            return "image/png";
        } else if (lowerName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerName.endsWith(".zip")) {
            return "application/zip";
        } else if (lowerName.endsWith(".java")) {
            return "text/x-java-source";
        } else {
            return "application/octet-stream";
        }
    }

    // Get file extension
    public String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    // Read file content
    public String readFile(String path) throws IOException {
        return FileUtils.readFileToString(new File(path), "UTF-8");
    }

    // Write file content
    public boolean writeFile(String path, String content) {
        try {
            FileUtils.writeStringToFile(new File(path), content, "UTF-8");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get temporary file path
    public String getTempFilePath(String prefix, String suffix) {
        String tempDir = Config.get("temp.dir", "D:/MyGitRepoStore/temp");
        createDirectory(tempDir);

        try {
            Path tempFile = Files.createTempFile(Paths.get(tempDir), prefix, suffix);
            return tempFile.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return tempDir + "/" + prefix + System.currentTimeMillis() + suffix;
        }
    }
}