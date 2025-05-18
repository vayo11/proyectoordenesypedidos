package com.hacom.orderservice.config;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppClient;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.impl.DefaultSmppSessionHandler;
import com.cloudhopper.smpp.pdu.PduRequest;
import com.cloudhopper.smpp.pdu.PduResponse;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationProperties(prefix = "spring.integration.smpp")
public class SmppConfig {

    private String host;
    private int port;
    private String systemId;
    private String password;
    private String systemType;
    private String sourceAddr;
    private String bindType;

    // Getters y Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getSourceAddr() {
        return sourceAddr;
    }

    public void setSourceAddr(String sourceAddr) {
        this.sourceAddr = sourceAddr;
    }

    public String getBindType() {
        return bindType;
    }

    public void setBindType(String bindType) {
        this.bindType = bindType;
    }

   @Profile("prod")  // Solo se carga en producción
   @ConditionalOnProperty(name = "smpp.enabled", havingValue = "true")
   @Bean(destroyMethod = "unbind")
    public SmppSession smppSession() throws SmppChannelException, UnrecoverablePduException,
            SmppTimeoutException, InterruptedException {
        SmppClient client = new DefaultSmppClient();

        SmppSessionConfiguration config = new SmppSessionConfiguration();
        config.setHost(host);
        config.setPort(port);
        config.setSystemId(systemId);
        config.setPassword(password);
        config.setSystemType(systemType);
        config.setType(convertirTipoBind(bindType));
        config.setName("TwilioSMPPConnector");
        config.setConnectTimeout(10000);
        config.setRequestExpiryTimeout(30000);

        return client.bind(config, new TwilioSmppSessionHandler());
    }

    private SmppBindType convertirTipoBind(String tipoBindStr) {
        if (tipoBindStr == null) {
            return SmppBindType.TRANSCEIVER;
        }

        String normalizado = tipoBindStr.toUpperCase();
        switch (normalizado) {
            case "TRANSCEIVER":
            case "TRX":
                return SmppBindType.TRANSCEIVER;
            case "TRANSMITTER":
            case "TX":
                return SmppBindType.TRANSMITTER;
            case "RECEIVER":
            case "RX":
                return SmppBindType.RECEIVER;
            default:
                throw new IllegalArgumentException("Tipo de bind no válido: " + tipoBindStr +
                        ". Valores válidos: TRANSCEIVER, TRANSMITTER o RECEIVER");
        }
    }

    private static class TwilioSmppSessionHandler extends DefaultSmppSessionHandler {
        @Override
        public PduResponse firePduRequestReceived(PduRequest pduRequest) {
            System.out.println("Twilio PDU received: " + pduRequest);
            return null;
        }

        @Override
        public void fireChannelUnexpectedlyClosed() {
            System.out.println("Conexión Twilio SMPP cerrada - intentando reconectar...");
        }
    }
}
