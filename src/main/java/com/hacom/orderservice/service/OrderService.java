package com.hacom.orderservice.service;

import akka.actor.ActorRef;
import com.hacom.grpc.OrderRequest;
import com.hacom.grpc.OrderResponse;
import com.hacom.orderservice.model.Order;
import com.hacom.orderservice.repository.OrderRepository;
import com.hacom.orderservice.smpp.SmppActor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.google.protobuf.Timestamp;
import java.time.Instant;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final ActorRef smppActor;

    public OrderService(OrderRepository orderRepository, ActorRef smppActor) {
        this.orderRepository = orderRepository;
        this.smppActor = smppActor;
    }

    public Mono<OrderResponse> processOrder(OrderRequest request) {
        logger.info("Processing new order: {}", request.getOrderId());

        // Convertir ítems del request a lista de strings con nombre y cantidad
        List<String> items = request.getItemsList().stream()
                .map(item -> item.getName() + " (x" + item.getQuantity() + ")")
                .collect(Collectors.toList());

        Order order = new Order();
        order.setId(new ObjectId());
        order.setOrderId(request.getOrderId());
        order.setCustomerId(request.getCustomerId());
        order.setCustomerPhoneNumber(request.getPhone());
        order.setItems(items);
        order.setStatus("RECEIVED");
        order.setTs(Instant.now());

        // Guardar en MongoDB con estado inicial "RECEIVED"
        return orderRepository.save(order)
                .doOnSuccess(savedOrder -> {
                    logger.info("Order saved to MongoDB with status RECEIVED: {}", savedOrder.getOrderId());
                    // Enviar al actor Akka para procesar el pedido
                    smppActor.tell(new SmppActor.ProcessOrder(savedOrder), ActorRef.noSender());
                })
                .map(savedOrder -> {
                    Instant instant = savedOrder.getTs(); 
                    Timestamp timestamp = Timestamp.newBuilder()
                            .setSeconds(instant.getEpochSecond())
                            .setNanos(instant.getNano())
                            .build();

                    return OrderResponse.newBuilder()
                            .setOrderId(savedOrder.getOrderId())
                            .setStatus(savedOrder.getStatus()) 
                            .setTimestamp(timestamp)
                            .build();
                });
    }

    public Mono<Order> findOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId);
    }

    public Flux<Order> findOrdersByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return orderRepository.findByTsBetween(startDate, endDate);
    }
}
