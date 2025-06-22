package com.septian.inventoryapp.controller;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.request.SaveItemRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.ItemResponse;
import com.septian.inventoryapp.service.IItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/item")
public class ItemController {
    @Autowired
    IItemService itemService;

    @GetMapping("/get/{id}")
    ResponseEntity<BaseResponse<ItemResponse>> getItemById(@PathVariable(name = "id") int id) throws ErrorException{
        BaseResponse<ItemResponse> item = itemService.getById(id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/getAll")
    ResponseEntity<Page<ItemResponse>> getAll(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) throws ErrorException{
        Pageable pageable = PageRequest.of(page,size, Sort.by("id").ascending());
        Page<ItemResponse> res = itemService.getAll(pageable);
        return ResponseEntity.ok(res);
    }

    @PostMapping("/add")
    ResponseEntity<BaseResponse<?>> addItem(@RequestBody SaveItemRequest item) throws ErrorException{
        itemService.saveItem(item);
        return ResponseEntity.ok(new BaseResponse<>("Added!",null));
    }

    @PutMapping("/update")
    ResponseEntity<BaseResponse<ItemResponse>> editItem(@RequestBody SaveItemRequest item) throws ErrorException{
        return ResponseEntity.ok(itemService.updateItem(item));
    }

    @DeleteMapping("/delete/{id}")
    ResponseEntity<BaseResponse<?>> deleteItem(@PathVariable(name = "id") Integer id) throws ErrorException {
        itemService.deleteItem(id);
        return ResponseEntity.ok(new BaseResponse<>("Deleted!",null));
    }
}
