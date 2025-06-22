package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.request.SaveOrderRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IOrderService {
    void saveOrder(SaveOrderRequest request);
    void updateOrder(SaveOrderRequest request);
    void deleteById(String orderId);
    BaseResponse<OrderResponse> getByIdOrder(String orderId);
    Page<OrderResponse> getAllOrder(Pageable pageable);
}
