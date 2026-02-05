package com.example.myhub.util;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: ZIP utility for creating and managing ZIP files
 * ============================================================================
 */

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    /**
     * Create ZIP file
     * @param sourceDir Source directory
     * @param outputZip Output ZIP file path
     * @return Whether successful
     */
    public static boolean createZip(String sourceDir, String outputZip) {
        FileOutputStream fos = null;
        ZipOutputStream zos = null;

        try {
            fos = new FileOutputStream(outputZip);
            zos = new ZipOutputStream(fos);

            File sourceFile = new File(sourceDir);
            if (!sourceFile.exists()) {
                return false;
            }

            addDirectoryToZip(sourceFile, sourceFile.getName(), zos);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (zos != null) zos.close();
                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Recursively add directory to ZIP
     */
    private static void addDirectoryToZip(File dir, String basePath, ZipOutputStream zos) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively process subdirectories
                String entryName = basePath + "/" + file.getName() + "/";
                zos.putNextEntry(new ZipEntry(entryName));
                zos.closeEntry();
                addDirectoryToZip(file, entryName, zos);
            } else {
                // Add file
                addFileToZip(file, basePath + "/" + file.getName(), zos);
            }
        }
    }

    /**
     * Add single file to ZIP
     */
    private static void addFileToZip(File file, String entryName, ZipOutputStream zos) throws IOException {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            ZipEntry zipEntry = new ZipEntry(entryName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }

            zos.closeEntry();
        } finally {
            if (fis != null) fis.close();
        }
    }

    /**
     * Create temporary ZIP file path
     * @param username Username
     * @param repoName Repository name
     * @param branch Branch name
     * @return ZIP file path
     */
    public static String getTempZipPath(String username, String repoName, String branch) {
        String tempDir = Config.get("temp.dir", "D:/MyGitRepoStore/temp");

        // Create temporary directory
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate unique filename
        String timestamp = String.valueOf(System.currentTimeMillis());
        String filename = username + "_" + repoName + "_" + branch + "_" + timestamp + ".zip";

        return tempDir + "/" + filename;
    }

    /**
     * Clean up old temporary files (keep last 7 days)
     * @param daysToKeep Days to keep
     */
    public static void cleanupOldTempFiles(int daysToKeep) {
        String tempDir = Config.get("temp.dir", "D:/MyGitRepoStore/temp");
        File dir = new File(tempDir);

        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isFile() && file.lastModified() < cutoffTime) {
                file.delete();
            }
        }
    }

    /**
     * Check if file is ZIP file
     */
    public static boolean isZipFile(String filename) {
        if (filename == null) return false;
        return filename.toLowerCase().endsWith(".zip");
    }
}