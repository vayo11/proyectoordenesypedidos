package com.hacom.orderservice.smpp;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.hacom.orderservice.model.Order;
import com.hacom.orderservice.service.SmppService;
import com.hacom.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SmppActor extends AbstractActor {

    private static final Logger logger = LoggerFactory.getLogger(SmppActor.class);

    public static final class ProcessOrder {
        public final Order order;

        public ProcessOrder(Order order) {
            this.order = order;
        }
    }

    private final SmppService smppService;
    private final OrderRepository orderRepository;
    private final boolean isProduction;

    private SmppActor(SmppService smppService, OrderRepository orderRepository, boolean isProduction) {
        this.smppService = smppService;
        this.orderRepository = orderRepository;
        this.isProduction = isProduction;
    }

    public static Props props(SmppService smppService, OrderRepository orderRepository, boolean isProduction) {
        return Props.create(SmppActor.class, () -> new SmppActor(smppService, orderRepository, isProduction));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ProcessOrder.class, this::processOrder)
                .build();
    }

    private void processOrder(ProcessOrder processOrder) {
        Order order = processOrder.order;
        logger.info("Processing order in Akka actor: {}", order.getOrderId());

        // Actualizar estado del pedido
        order.setStatus("COMPLETED");

        if (isProduction) {
            String message = "Your order " + order.getOrderId() + " has been processed";
            smppService.sendSms(order.getCustomerPhoneNumber(), message);
            logger.info("SMS sent for order {}", order.getOrderId());
        } else {
            logger.info("SMS sending skipped for order {} (development mode)", order.getOrderId());
        }

        // Guardar actualización en MongoDB (reactivo)
        orderRepository.save(order)
                .doOnSuccess(savedOrder -> logger.info("Order status updated to COMPLETED for {}", savedOrder.getOrderId()))
                .doOnError(error -> logger.error("Error saving order status update", error))
                .subscribe();
    }
}
