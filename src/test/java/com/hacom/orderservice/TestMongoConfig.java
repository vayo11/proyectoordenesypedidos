package com.hacom.orderservice;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@TestConfiguration
@Profile("test")
public class TestMongoConfig {
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), "testdb");
    }
    
    @Bean(destroyMethod = "close")
    public MongoClient mongoClient() throws Exception {
        // Configura MongoDB embebido en el puerto 27017
        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodConfig config = MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(new Net("localhost", 27017, false))
            .build();
        
        MongodExecutable executable = starter.prepare(config);
        executable.start();
        
        return MongoClients.create("mongodb://localhost:27017");
    }
}