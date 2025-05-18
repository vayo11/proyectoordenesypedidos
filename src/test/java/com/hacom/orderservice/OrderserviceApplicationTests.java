package com.hacom.orderservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Asegúrate de tener un perfil 'test' configurado
@Import(TestMongoConfig.class)
class OrderserviceApplicationTests {

    @Test
    void contextLoads() {
        // Este test solo verifica que el contexto de Spring se carga correctamente
    }
}