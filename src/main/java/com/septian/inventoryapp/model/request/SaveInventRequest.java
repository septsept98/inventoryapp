package com.septian.inventoryapp.model.request;

public class SaveInventRequest {
    private Integer id;
    private Integer itemId;
    private int qty;
    private String type;

    public SaveInventRequest() {
    }

    public SaveInventRequest(Integer id, Integer itemId, int qty, String type) {
        this.id = id;
        this.itemId = itemId;
        this.qty = qty;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
