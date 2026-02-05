package com.example.myhub.servlet;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Servlet for handling repository downloads
 * ============================================================================
 */

import com.example.myhub.util.JGitUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DownloadServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();

        // Parse path: /owner/repo/archive/branch.zip
        if (pathInfo == null || pathInfo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path");
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length < 4) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid path format");
            return;
        }

        String owner = parts[1];
        String repo = parts[2];
        // parts[3] is "archive"
        String branchZip = parts.length > 4 ? parts[4] : "main.zip";
        String branch = branchZip.replace(".zip", "");

        try {
            // Get repository path
            String repoPath = JGitUtil.getRepoPath(owner, repo);
            Git git = JGitUtil.openRepository(repoPath);

            if (git == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Repository not found");
                return;
            }

            Repository repository = git.getRepository();

            // Get branch's latest commit
            ObjectId branchId = repository.resolve(branch);
            if (branchId == null) {
                git.close();
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Branch not found: " + branch);
                return;
            }

            // Set response headers
            String fileName = repo + "-" + branch + ".zip";
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Create ZIP output stream
            try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {

                // Traverse Git tree and add files to ZIP
                try (RevWalk revWalk = new RevWalk(repository)) {
                    RevCommit commit = revWalk.parseCommit(branchId);
                    RevTree tree = commit.getTree();

                    try (TreeWalk treeWalk = new TreeWalk(repository)) {
                        treeWalk.addTree(tree);
                        treeWalk.setRecursive(true);

                        while (treeWalk.next()) {
                            String filePath = repo + "-" + branch + "/" + treeWalk.getPathString();

                            // Add file to ZIP
                            ZipEntry zipEntry = new ZipEntry(filePath);
                            zipOut.putNextEntry(zipEntry);

                            // Read file content
                            ObjectId objectId = treeWalk.getObjectId(0);
                            ObjectLoader loader = repository.open(objectId);
                            loader.copyTo(zipOut);

                            zipOut.closeEntry();
                        }
                    }
                }

                zipOut.finish();
            }

            git.close();

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Error creating ZIP file: " + e.getMessage());
        }
    }
}
