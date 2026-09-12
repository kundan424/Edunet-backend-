package com.edtech.platform.media.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

public class MediaStorageConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(LocalMediaStorageServiceImpl.class, B2MediaStorageServiceImpl.class);

    @Test
    void defaultStorageIsLocal() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(LocalMediaStorageServiceImpl.class);
            assertThat(context).doesNotHaveBean(B2MediaStorageServiceImpl.class);
        });
    }

    @Test
    void explicitLocalStorage() {
        contextRunner
                .withPropertyValues("edtech.media.storage-type=LOCAL")
                .run(context -> {
                    assertThat(context).hasSingleBean(LocalMediaStorageServiceImpl.class);
                    assertThat(context).doesNotHaveBean(B2MediaStorageServiceImpl.class);
                });
    }

    @Test
    void explicitB2Storage() {
        contextRunner
                .withPropertyValues(
                        "edtech.media.storage-type=B2",
                        "edtech.b2.endpoint=https://s3.fake.com",
                        "edtech.b2.bucket=test-bucket",
                        "edtech.b2.access-key=test",
                        "edtech.b2.secret-key=test",
                        "edtech.b2.region=auto"
                )
                .run(context -> {
                    assertThat(context).hasSingleBean(B2MediaStorageServiceImpl.class);
                    assertThat(context).doesNotHaveBean(LocalMediaStorageServiceImpl.class);
                });
    }
}
