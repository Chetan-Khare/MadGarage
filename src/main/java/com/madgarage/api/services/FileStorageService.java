package com.madgarage.api.services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final String UPLOAD_REL = "src/main/resources/static/uploads/";
    private static final String GUIDES_REL = "src/main/resources/static/guides/";

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
}
