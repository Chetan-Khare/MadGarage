package com.madgarage.api.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FileStorageService.class);
    private static final String UPLOAD_REL = System.getenv("UPLOAD_DIR") != null 
        ? System.getenv("UPLOAD_DIR") + "/uploads/" 
        : "data/uploads/";
    
    private static final String GUIDES_REL = System.getenv("UPLOAD_DIR") != null 
        ? System.getenv("UPLOAD_DIR") + "/guides/" 
        : "data/guides/";

    public String saveImage(byte[] bytes, String extension) throws IOException {
        return saveFile(bytes, extension, UPLOAD_REL, "/uploads/");
    }

    public String saveGuide(byte[] bytes, String extension) throws IOException {
        return saveFile(bytes, extension, GUIDES_REL, "/guides/");
    }

    private String saveFile(byte[] bytes, String extension, String relPath, String urlPrefix) throws IOException {
        Path base = Paths.get(System.getProperty("user.dir"), relPath).toAbsolutePath().normalize();
        if (!base.toFile().exists()) {
            base.toFile().mkdirs();
        }
        String fileName = UUID.randomUUID() + "." + extension.replaceAll("[^a-zA-Z0-9]", "");
        Path target = base.resolve(fileName).normalize();
        if (!target.startsWith(base)) {
            throw new IllegalArgumentException("Path traversal detected");
        }
        Files.write(target, bytes);
        return urlPrefix + fileName;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        String relPath;
        if (fileUrl.startsWith("/uploads/")) {
            relPath = UPLOAD_REL;
        } else if (fileUrl.startsWith("/guides/")) {
            relPath = GUIDES_REL;
        } else {
            return; // Not a managed file path
        }

        try {
            String fileName = Paths.get(new java.net.URI(fileUrl).getPath()).getFileName().toString();
            Path base = Paths.get(System.getProperty("user.dir"), relPath).toAbsolutePath().normalize();
            Path target = base.resolve(fileName).normalize();

            // Safety check: Ensure target is within base directory
            if (target.startsWith(base) && Files.exists(target)) {
                Files.delete(target);
            }
        } catch (Exception e) {
            // Log error but don't fail the request (the DB record update is more important)
            log.error("Failed to delete physical file: " + fileUrl, e);
        }
    }
}
