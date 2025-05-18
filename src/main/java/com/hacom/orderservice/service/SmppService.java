package com.hacom.orderservice.service;

import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Profile;

@Service
@Profile("!dev") 
public class SmppService {

    private static final Logger logger = LoggerFactory.getLogger(SmppService.class);
    
    private final SmppSession smppSession;

    public SmppService(SmppSession smppSession) {
        this.smppSession = smppSession;
    }

    public void sendSms(String phoneNumber, String message) {
        try {
            SubmitSm submitSm = createSubmitSm(phoneNumber, message);
            smppSession.submit(submitSm, 10000);
            logger.info("SMS enviado exitosamente a {}: {}", phoneNumber, message);
        } catch (Exception e) {
            handleSmsSendingError(phoneNumber, e);
            throw new SmsSendingException("Error al enviar SMS a " + phoneNumber, e);
        }
    }

    private SubmitSm createSubmitSm(String phoneNumber, String message) 
            throws SmppInvalidArgumentException {
        SubmitSm submitSm = new SubmitSm();
        Address destAddress = new Address();
        destAddress.setAddress(phoneNumber);
        destAddress.setTon((byte) 1);  // Tipo de número internacional
        destAddress.setNpi((byte) 1);  // Plan de numeración ISDN/E.164
        
        submitSm.setDestAddress(destAddress);
        
        // Validación y conversión segura del mensaje a bytes
        byte[] messageBytes = message.getBytes();
        if (messageBytes.length > 255) {
            throw new SmppInvalidArgumentException("El mensaje es demasiado largo. Máximo 255 bytes");
        }
        submitSm.setShortMessage(messageBytes);
        
        return submitSm;
    }

    private void handleSmsSendingError(String phoneNumber, Exception e) {
        logger.error("Error al enviar SMS a {}: {}", phoneNumber, e.getMessage());
        
        if (e instanceof InterruptedException) {
            Thread.currentThread().interrupt();
        } else if (e instanceof UnrecoverablePduException || 
                  e instanceof SmppTimeoutException || 
                  e instanceof SmppChannelException) {
            logger.warn("Error de conexión SMPP, se intentará reconectar");
        }
    }

    public static class SmsSendingException extends RuntimeException {
        public SmsSendingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}