package com.septian.inventoryapp.controller;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.request.SaveOrderRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.service.IOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {
    @Autowired
    IOrderService iOrderService;

    @PostMapping("/add")
    ResponseEntity<BaseResponse<?>> addOrder(@RequestBody SaveOrderRequest request) throws ErrorException {
        iOrderService.saveOrder(request);
        return ResponseEntity.ok(new BaseResponse<>("Added!",null));
    }

    @PutMapping("/update")
    ResponseEntity<BaseResponse<?>> updateOrder(@RequestBody SaveOrderRequest request) throws ErrorException{
        iOrderService.updateOrder(request);
        return ResponseEntity.ok(new BaseResponse<>("Updated!",null));
    }

    @DeleteMapping("/delete/{orderId}")
    ResponseEntity<BaseResponse<?>> deleteOrder(@PathVariable String orderId) throws ErrorException{
        iOrderService.deleteById(orderId);
        return ResponseEntity.ok(new BaseResponse<>("Deleted!",null));
    }

    @GetMapping("/get/{orderId}")
    ResponseEntity<BaseResponse<?>> getByIdOrder(@PathVariable String orderId) throws ErrorException{
        return ResponseEntity.ok(iOrderService.getByIdOrder(orderId));
    }

    @GetMapping("/getAll")
    ResponseEntity<Page<?>> getAllOrder(@RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "0") int page) throws ErrorException{
        Pageable pageable = PageRequest.of(page,size, Sort.by("orderNo").ascending());
        return ResponseEntity.ok(iOrderService.getAllOrder(pageable));
    }
}
