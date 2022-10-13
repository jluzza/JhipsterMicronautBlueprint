package com.luzza.micronaut.blueprint;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.GenericContainer;

import java.util.concurrent.atomic.AtomicBoolean;

public class RedisTestContainerExtension implements BeforeAllCallback {

    private static AtomicBoolean started = new AtomicBoolean(false);

    private static GenericContainer redis;

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        if (!started.get()) {
            redis = new GenericContainer("redis:6.0.10").withExposedPorts(6379);
            redis.start();
            System.setProperty(
                "jhipster.cache.redis.server",
                String.format("redis://%s:%s", redis.getContainerIpAddress(), redis.getMappedPort(6379))
            );
            started.set(true);
        }
    }
}