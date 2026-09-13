package com.edtech.platform.media.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
    "MEDIA_STORAGE_TYPE=B2",
    "edtech.b2.endpoint=https://s3.us-west-004.backblazeb2.com",
    "edtech.b2.bucket=test-bucket",
    "edtech.b2.access-key=test-access-key",
    "edtech.b2.secret-key=test-secret-key",
    "edtech.b2.region=us-west-004"
})
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
