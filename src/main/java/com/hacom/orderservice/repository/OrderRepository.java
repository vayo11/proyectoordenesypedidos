package com.hacom.orderservice.repository;

import com.hacom.orderservice.model.Order;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

public interface OrderRepository extends ReactiveMongoRepository<Order, Object> {

    Mono<Order> findByOrderId(String orderId);

    Flux<Order> findByTsBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}