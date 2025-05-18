package com.hacom.orderservice.service.mock;

import com.hacom.orderservice.service.SmppService;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("!prod")
@Service
public class SmppServiceMock extends SmppService {
    public SmppServiceMock() {
        super(null);  
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        LoggerFactory.getLogger(SmppServiceMock.class)
            .info("(MOCK) SMS enviado a {}: {}", phoneNumber, message);
    }
}