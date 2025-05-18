package com.hacom.orderservice.model;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "orders")
public class Order {

    @Id
    private ObjectId id;
    private String orderId;
    private String customerId;
    private String customerPhoneNumber;
    private String status;
    private List<String> items;
    private Instant ts;

    // Constructors
    public Order() {
        this.ts = Instant.now(); // Cambiado de OffsetDateTime.now() a Instant.now()
    }

    public Order(String orderId, String customerId, String customerPhoneNumber, String status, List<String> items) {
        this();
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerPhoneNumber = customerPhoneNumber;
        this.status = status;
        this.items = items;
    }

    // Getters and Setters
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    public Instant getTs() {
        return ts;
    }

    public void setTs(Instant ts) {
        this.ts = ts;
    }
}
