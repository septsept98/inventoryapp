package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.request.SaveItemRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.ItemResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IItemService {
    BaseResponse<ItemResponse> getById(Integer id);
    void saveItem(SaveItemRequest itemRequest);
    BaseResponse<ItemResponse> updateItem(SaveItemRequest itemRequest);
    void deleteItem(int id);
    Page<ItemResponse> getAll(Pageable pageable);
}
