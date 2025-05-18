package com.hacom.orderservice.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class GrpcServerRunner {

    private Server server;

    private final OrderServiceImpl orderServiceImpl;

    @Value("${grpc.server.port:9090}")
    private int grpcPort;

    public GrpcServerRunner(OrderServiceImpl orderServiceImpl) {
        this.orderServiceImpl = orderServiceImpl;
    }

    @PostConstruct
    public void startGrpcServer() throws IOException {
        this.server = ServerBuilder
                .forPort(grpcPort)
                .addService(orderServiceImpl)  // Registra tu servicio aquí
                .maxInboundMessageSize(16 * 1024 * 1024) // 16MB
                .keepAliveTime(30, TimeUnit.SECONDS)
                .keepAliveTimeout(5, TimeUnit.SECONDS)
                .permitKeepAliveWithoutCalls(true)
                .build()
                .start();

        System.out.println("🚀 gRPC server started on port " + grpcPort);

        // Para que no se cierre el proceso (esto no es obligatorio si Spring mantiene la app viva)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("🛑 Shutdown hook triggered. Stopping gRPC server...");
            stop();
        }));
    }

    @PreDestroy
    public void stop() {
        if (server != null) {
            server.shutdown();
            try {
                if (!server.awaitTermination(30, TimeUnit.SECONDS)) {
                    server.shutdownNow();
                }
            } catch (InterruptedException e) {
                server.shutdownNow();
            }
        }
    }
}