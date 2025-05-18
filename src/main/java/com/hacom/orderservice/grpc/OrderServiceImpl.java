package com.hacom.orderservice.grpc;

import com.hacom.grpc.OrderRequest;
import com.hacom.grpc.OrderResponse;
import com.hacom.grpc.OrderServiceGrpc;
import com.hacom.orderservice.service.OrderService;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderService orderService;

    @Autowired
    public OrderServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public void createOrder(OrderRequest request,
            StreamObserver<OrderResponse> responseObserver) {
        logger.info("Received gRPC request for order: {}", request.getOrderId());

        orderService.processOrder(request)
                .subscribe(
                        response -> {
                            responseObserver.onNext(response);
                            responseObserver.onCompleted();
                            logger.info("gRPC response sent for order: {}", request.getOrderId());
                        },
                        error -> {
                            logger.error("Error processing gRPC request", error);
                            responseObserver.onError(
                                    io.grpc.Status.INTERNAL
                                            .withDescription("Error interno en el servidor: " + error.getMessage())
                                            .withCause(error)
                                            .asRuntimeException());
                        }

                );
    }
}