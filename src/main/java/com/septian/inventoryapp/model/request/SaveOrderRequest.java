package com.septian.inventoryapp.model.request;

import java.math.BigDecimal;

public class SaveOrderRequest {
    private String orderId;
    private Integer itemId;
    private int qty;
    private BigDecimal price;

    public SaveOrderRequest(String orderId, Integer itemId, int qty, BigDecimal price) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.qty = qty;
        this.price = price;
    }

    public SaveOrderRequest() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
