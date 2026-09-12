package com.edtech.platform.media.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

@SpringBootTest
public class MediaStorageOsEnvTest {
    @Autowired
    private ApplicationContext context;
    @Autowired
    private Environment env;

    @Test
    void checkBeans() {
        System.out.println("Property: " + env.getProperty("edtech.media.storage-type"));
        String[] beans = context.getBeanNamesForType(MediaStorageService.class);
        System.out.println("Found MediaStorageService beans: " + beans.length);
        for(String b: beans) System.out.println(" - " + b);
    }
}
