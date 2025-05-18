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

    private MongodExecutable mongodExecutable;

    @Bean(destroyMethod = "stop")
    public MongodExecutable mongodExecutable() throws Exception {
        MongodStarter starter = MongodStarter.getDefaultInstance();
        MongodConfig config = MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(new Net("localhost", 27017, false))
            .build();

        mongodExecutable = starter.prepare(config);
        mongodExecutable.start();
        return mongodExecutable;
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "testdb");
    }
}
