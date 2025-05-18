package com.hacom.orderservice;

import com.hacom.orderservice.TestMongoConfig;
import com.hacom.orderservice.service.OrderService;
import com.hacom.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestMongoConfig.class)
class OrderserviceApplicationTests {

    // Mocks para que Spring no intente cargar beans reales
    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @Test
    void contextLoads() {
        // Validación mínima de carga de contexto
        System.out.println("Contexto cargado correctamente.");
    }
}
