package com.biblioteca.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class MongoTestConfig {

    private static final MongoDBContainer container = new MongoDBContainer(DockerImageName.parse("mongo:6.0"));

    static {
        container.start();
        System.setProperty("spring.data.mongodb.uri", container.getReplicaSetUrl());
    }

    @Bean
    public MongoDBContainer mongoDBContainer() {
        return container;
    }
}