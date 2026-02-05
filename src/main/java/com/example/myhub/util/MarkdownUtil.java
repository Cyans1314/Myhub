package com.example.myhub.util;

/**
 * ============================================================================
 * Author: Cyans
 * From: Chang'an University
 * Date: 2025-12-23
 * Description: Markdown utility for converting Markdown to HTML
 * ============================================================================
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownUtil {

    /**
     * Simple Markdown to HTML conversion (basic functionality)
     * Supports: headings, bold, italic, code blocks, links
     * @param markdown Markdown text
     * @return HTML text
     */
    public static String markdownToHtml(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        String html = markdown;

        // 1. Headings (# to ######)
        html = html.replaceAll("(?m)^###### (.*)$", "<h6>$1</h6>");
        html = html.replaceAll("(?m)^##### (.*)$", "<h5>$1</h5>");
        html = html.replaceAll("(?m)^#### (.*)$", "<h4>$1</h4>");
        html = html.replaceAll("(?m)^### (.*)$", "<h3>$1</h3>");
        html = html.replaceAll("(?m)^## (.*)$", "<h2>$1</h2>");
        html = html.replaceAll("(?m)^# (.*)$", "<h1>$1</h1>");

        // 2. Bold (**text**)
        html = html.replaceAll("\\*\\*(.*?)\\*\\*", "<strong>$1</strong>");
        html = html.replaceAll("__(.*?)__", "<strong>$1</strong>");

        // 3. Italic (*text*)
        html = html.replaceAll("\\*(.*?)\\*", "<em>$1</em>");
        html = html.replaceAll("_(.*?)_", "<em>$1</em>");

        // 4. Inline code (`code`)
        html = html.replaceAll("`(.*?)`", "<code>$1</code>");

        // 5. Code block (```code```)
        Pattern codeBlockPattern = Pattern.compile("```(.*?)```", Pattern.DOTALL);
        Matcher matcher = codeBlockPattern.matcher(html);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String code = matcher.group(1).trim();
            String replacement = "<pre><code>" + escapeHtml(code) + "</code></pre>";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        html = sb.toString();

        // 6. Image ![alt](src) - must be processed before links
        html = html.replaceAll("!\\[(.*?)\\]\\((.*?)\\)", "<img src=\"$2\" alt=\"$1\" />");

        // 7. Link [text](url)
        html = html.replaceAll("\\[(.*?)\\]\\((.*?)\\)", "<a href=\"$2\" target=\"_blank\">$1</a>");

        // 8. Line break (two spaces + newline or direct newline)
        html = html.replaceAll("  \\n", "<br/>\n");

        // 9. Paragraph (wrap continuous text in p tags)
        String[] lines = html.split("\\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }

            // If already HTML tag (h1-h6, pre, code, etc.), add directly
            if (line.startsWith("<h") || line.startsWith("<pre") ||
                    line.startsWith("<a") || line.startsWith("<img") ||
                    line.startsWith("<code") || line.startsWith("<strong") ||
                    line.startsWith("<em") || line.startsWith("</")) {
                result.append(line).append("\n");
            } else {
                // Otherwise wrap in p tag
                result.append("<p>").append(line).append("</p>\n");
            }
        }

        return result.toString();
    }

    /**
     * HTML escape
     */
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * Check if Markdown file
     * @param filename Filename
     * @return Whether .md file
     */
    public static boolean isMarkdownFile(String filename) {
        if (filename == null) return false;
        return filename.toLowerCase().endsWith(".md") ||
                filename.toLowerCase().endsWith(".markdown");
    }
}