package com.septian.inventoryapp.model.response;

import java.math.BigDecimal;

public class InventoryResponse {
    private int id;
    private int itemId;
    private String itemName;
    private BigDecimal itemPrice;
    private int qtyItem;
    private String type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getQtyItem() {
        return qtyItem;
    }

    public void setQtyItem(int qtyItem) {
        this.qtyItem = qtyItem;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
