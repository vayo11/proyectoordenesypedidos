package com.hacom.orderservice.service.config;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.hacom.orderservice.repository.OrderRepository;
import com.hacom.orderservice.service.SmppService;
import com.hacom.orderservice.smpp.SmppActor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AkkaConfig {

    @Bean
    public ActorSystem actorSystem() {
        return ActorSystem.create("OrderServiceSystem");
    }

    @Bean
    public ActorRef smppActor(ActorSystem actorSystem, SmppService smppService, OrderRepository orderRepository) {
        boolean isProduction = false; // puedes parametrizarlo desde application.properties o @Value
        return actorSystem.actorOf(SmppActor.props(smppService, orderRepository, isProduction), "smppActor");
    }
}