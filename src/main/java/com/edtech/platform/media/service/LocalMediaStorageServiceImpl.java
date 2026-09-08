package com.edtech.platform.media.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class LocalMediaStorageServiceImpl implements MediaStorageService {

    private final Path rootLocation;

    public LocalMediaStorageServiceImpl(@Value("${media.storage.location:./data/media/}") String storageLocation) {
        this.rootLocation = Paths.get(storageLocation);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Failed to store empty file.");
        }
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String storageKey = UUID.randomUUID().toString() + extension;
        Path destinationFile = this.rootLocation.resolve(Paths.get(storageKey))
                .normalize().toAbsolutePath();
                
        if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new SecurityException("Cannot store file outside current directory.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return storageKey;
    }

    @Override
    public void delete(String storageKey) throws IOException {
        if (!StringUtils.hasText(storageKey)) {
            return;
        }
        Path file = rootLocation.resolve(storageKey).normalize().toAbsolutePath();
        if (!file.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new SecurityException("Cannot delete file outside current directory.");
        }
        Files.deleteIfExists(file);
    }

    @Override
    public Resource getResource(String storageKey) throws IOException {
        Path file = rootLocation.resolve(storageKey).normalize().toAbsolutePath();
        if (!file.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new SecurityException("Cannot access file outside current directory.");
        }
        Resource resource = new UrlResource(file.toUri());
        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("Could not read file: " + storageKey);
        }
    }
}
