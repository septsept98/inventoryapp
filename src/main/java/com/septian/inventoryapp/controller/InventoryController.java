package com.septian.inventoryapp.controller;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.request.SaveInventRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.InventoryResponse;
import com.septian.inventoryapp.service.IInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    @Autowired
    IInventoryService inventoryService;

    @PostMapping("/add")
    ResponseEntity<BaseResponse<?>> addInvent(@RequestBody SaveInventRequest request) throws ErrorException {
        inventoryService.saveInventory(request, "ADD");
        return ResponseEntity.ok(new BaseResponse<>("Added!",null));
    }

    @GetMapping("/get/{id}")
    ResponseEntity<BaseResponse<InventoryResponse>> getById(@PathVariable int id) throws ErrorException{
        return ResponseEntity.ok(inventoryService.getById(id));
    }

    @GetMapping("/getAll")
    ResponseEntity<Page<InventoryResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size) throws ErrorException{
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").ascending());
        return ResponseEntity.ok(inventoryService.findAll(pageable));
    }

    @PutMapping("/update")
    ResponseEntity<BaseResponse<?>> updateInvent(@RequestBody SaveInventRequest request) throws ErrorException{
        inventoryService.updateInventory(request,"UPDATE");
        return ResponseEntity.ok(new BaseResponse<>("UPDATED!", null));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<BaseResponse<?>> deleteById(@PathVariable int id) throws ErrorException{
        inventoryService.deleteById(id);
        return ResponseEntity.ok(new BaseResponse<>("DELETED", null));
    }
}
