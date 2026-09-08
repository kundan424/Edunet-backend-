package com.edtech.platform.media.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaStorageService {
    String store(MultipartFile file) throws IOException;
    void delete(String storageKey) throws IOException;
    Resource getResource(String storageKey) throws IOException;
}
