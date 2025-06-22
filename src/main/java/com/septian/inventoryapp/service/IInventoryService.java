package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.request.SaveInventRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.InventoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInventoryService {
    void saveInventory(SaveInventRequest inventRequest, String method);
    void updateInventory(SaveInventRequest inventRequest, String method);
    BaseResponse<InventoryResponse> getById(int id);
    void deleteById(int id);
    Page<InventoryResponse> findAll(Pageable pageable);
}
