package com.edtech.platform.media.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"MEDIA_STORAGE_TYPE=B2"})
public class MediaStorageEnvironmentTest {

    @Autowired
    private Environment env;

    @Autowired(required = false)
    private MediaStorageService mediaStorageService;

    @Test
    void testPropertyResolution() {
        System.out.println("Property edtech.media.storage-type: " + env.getProperty("edtech.media.storage-type"));
        System.out.println("Property MEDIA_STORAGE_TYPE: " + env.getProperty("MEDIA_STORAGE_TYPE"));
        System.out.println("MediaStorageService instance: " + (mediaStorageService != null ? mediaStorageService.getClass().getName() : "null"));
    }
}
