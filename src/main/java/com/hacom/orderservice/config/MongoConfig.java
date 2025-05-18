package com.hacom.orderservice.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.hacom.orderservice.repository")
public class MongoConfig {

    private final String mongoUri;
    private final String databaseName;

    // Inyección por constructor (recomendado)
    public MongoConfig(
            @Value("${spring.data.mongodb.uri}") String mongoUri,
            @Value("${spring.data.mongodb.database}") String databaseName) {
        this.mongoUri = mongoUri;
        this.databaseName = databaseName;
    }

    @Bean
    public MongoClient reactiveMongoClient() {
        // Validación adicional
        if (!mongoUri.startsWith("mongodb://") && !mongoUri.startsWith("mongodb+srv://")) {
            throw new IllegalArgumentException("URI de MongoDB inválida");
        }
        return MongoClients.create(mongoUri);
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), databaseName);
    }
}