package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.entity.InventoryEntity;
import com.septian.inventoryapp.model.entity.ItemEntity;
import com.septian.inventoryapp.model.request.SaveInventRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.InventoryResponse;
import com.septian.inventoryapp.repository.InventoryReposity;
import com.septian.inventoryapp.repository.ItemRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Optional;

@Service
public class InventoryService implements IInventoryService{
    public final InventoryReposity inventReposity;
    public final ItemRepository itemRepository;

    @Autowired
    public InventoryService(ItemRepository itemRepository, InventoryReposity inventReposity) {
        this.itemRepository = itemRepository;
        this.inventReposity = inventReposity;
    }

    @Override
    public void saveInventory(SaveInventRequest inventRequest, String method) {
        validateRequest(inventRequest, method);
        InventoryEntity inventoryEntity = new InventoryEntity();
        Optional<ItemEntity> itemOptional = itemRepository.findById(inventRequest.getItemId());
        if (itemOptional.isPresent()){
            InventoryEntity inventItemExistT = inventReposity.getByItemIdAndType(inventRequest.getItemId(), "T");
            InventoryEntity inventItemExistW = inventReposity.getByItemIdAndType(inventRequest.getItemId(), "W");
            if (!ObjectUtils.isEmpty(inventItemExistT)){
                validateUpdateItemId(inventRequest, inventItemExistT);
                if (inventRequest.getType().equalsIgnoreCase("T")){
                    inventItemExistT.setQty(inventItemExistT.getQty()+inventRequest.getQty());
                } else {
                    if (inventItemExistT.getQty() != 0 && inventItemExistT.getQty() >= inventRequest.getQty()){
                        inventItemExistT.setQty(inventItemExistT.getQty() - inventRequest.getQty());
                    } else {
                        throw new ErrorException("The Item Stock is Insufficient","Bad Request", HttpStatus.BAD_REQUEST);
                    }
                }
                inventReposity.save(inventItemExistT);
            }

            if (ObjectUtils.isEmpty(inventItemExistT) && inventRequest.getType().equalsIgnoreCase("W")){
                throw new ErrorException("Top Up Inventory First", "Bad Request", HttpStatus.BAD_REQUEST);
            }

            if (!ObjectUtils.isEmpty(inventItemExistW)){
                validateUpdateItemId(inventRequest, inventItemExistW);
                if (inventRequest.getType().equalsIgnoreCase("W")) {
                    inventItemExistW.setQty(inventItemExistW.getQty() + inventRequest.getQty());
                    inventReposity.save(inventItemExistW);
                }
            }
            if (ObjectUtils.isEmpty(inventItemExistT) && ObjectUtils.isEmpty(inventItemExistW)) {
                inventoryEntity.setQty(inventRequest.getQty());
                inventoryEntity.setItem(itemOptional.get());
                inventoryEntity.setType(inventRequest.getType().toUpperCase());
                inventReposity.save(inventoryEntity);
            }
            if (!ObjectUtils.isEmpty(inventItemExistT) && ObjectUtils.isEmpty(inventItemExistW) && inventRequest.getType().equalsIgnoreCase("W")){
                inventoryEntity.setQty(inventRequest.getQty());
                inventoryEntity.setItem(itemOptional.get());
                inventoryEntity.setType(inventRequest.getType().toUpperCase());
                inventReposity.save(inventoryEntity);
            }
            if (ObjectUtils.isEmpty(inventItemExistT) && inventRequest.getType().equalsIgnoreCase("T")){
                inventoryEntity.setQty(inventRequest.getQty());
                inventoryEntity.setItem(itemOptional.get());
                inventoryEntity.setType(inventRequest.getType().toUpperCase());
                inventReposity.save(inventoryEntity);
            }
        } else {
            throw new ErrorException("Item Not Found","Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public void updateInventory(SaveInventRequest inventRequest, String method) {
        validateRequest(inventRequest, method);
        Optional<ItemEntity> itemOptional = itemRepository.findById(inventRequest.getItemId());
        if (itemOptional.isPresent()) {
            Optional<InventoryEntity> inventItemExistOptional = inventReposity.findById(inventRequest.getId());
            if (inventItemExistOptional.isPresent()){
                InventoryEntity inventExist = inventItemExistOptional.get();
                if (inventExist.getItem().getId() != inventRequest.getItemId()){
                    throw new ErrorException("Cannot Change Item", "Bad Request", HttpStatus.BAD_REQUEST);
                }

                InventoryEntity inventItemExistT = inventReposity.getByItemIdAndType(inventRequest.getItemId(), "T");
                InventoryEntity inventItemExistW = inventReposity.getByItemIdAndType(inventRequest.getItemId(), "W");

                if (!inventExist.getType().equalsIgnoreCase(inventRequest.getType())){
                    if (!ObjectUtils.isEmpty(inventItemExistT) && !ObjectUtils.isEmpty(inventItemExistW)) {
                        throw new ErrorException("Cannot Change Type","Bad Request", HttpStatus.BAD_REQUEST);
                    }
                    if (inventRequest.getType().equalsIgnoreCase("T") && ObjectUtils.isEmpty(inventItemExistT)) {
                        inventExist.setType(inventRequest.getType());
                    }
                    if (inventRequest.getType().equalsIgnoreCase("W") && ObjectUtils.isEmpty(inventItemExistW)) {
                        inventExist.setType(inventRequest.getType());
                    }
                }
                inventExist.setQty(inventRequest.getQty());
                inventReposity.save(inventExist);
            } else {
                throw new ErrorException("Invent Not Found","Bad Request", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ErrorException("Item Not Found","Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public BaseResponse<InventoryResponse> getById(int id) {
        Optional<InventoryEntity> invenOptional = inventReposity.findById(id);
        InventoryResponse inventoryResponse = new InventoryResponse();
        if (invenOptional.isPresent()){
            InventoryEntity invent = invenOptional.get();
            inventoryResponse.setId(invent.getId());
            inventoryResponse.setItemId(invent.getItem().getId());
            inventoryResponse.setItemName(invent.getItem().getName());
            inventoryResponse.setItemPrice(invent.getItem().getPrice());
            inventoryResponse.setQtyItem(invent.getQty());
            inventoryResponse.setType(invent.getType().equals("T")?"Top Up":"Withdrawal");
        } else {
            throw new ErrorException("Inventory Not Found", "Bad Request", HttpStatus.BAD_REQUEST);
        }
        return new BaseResponse<>("Success",inventoryResponse);
    }

    @Override
    public void deleteById(int id) {
        Optional<InventoryEntity> inventoryEntity = inventReposity.findById(id);
        if (inventoryEntity.isPresent()){
            inventReposity.deleteById(id);
        } else {
            throw new ErrorException("Inventory Not Found", "Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Page<InventoryResponse> findAll(Pageable pageable) {
        Page<InventoryEntity> pageEntity = inventReposity.findAll(pageable);
        return pageEntity.map(entity -> {
            InventoryResponse response = new InventoryResponse();
            response.setId(entity.getId());
            response.setType(entity.getType().equals("T")?"Top Up":"Withdrawal");
            response.setQtyItem(entity.getQty());
            response.setItemId(entity.getItem().getId());
            response.setItemName(entity.getItem().getName());
            response.setItemPrice(entity.getItem().getPrice());
            return response;
        });
    }

    public void validateRequest(SaveInventRequest inventRequest, String method){
        if (method.equals("UPDATE")){
            if (inventRequest.getId() == null){
                throw new ErrorException("Id Cannot be Null", "Bad Request", HttpStatus.BAD_REQUEST);
            }
        }

        if (inventRequest.getItemId() == null){
            throw new ErrorException("ItemId Cannot be Null", "Bad Request", HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isBlank(inventRequest.getType())){
            throw new ErrorException("Type Cannot be Null", "Bad Request", HttpStatus.BAD_REQUEST);
        }

        if (inventRequest.getQty()<=0){
            throw new ErrorException("The Quantity Must be Greater Than 0", "Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    public void validateUpdateItemId(SaveInventRequest inventRequest, InventoryEntity inventItemExist){
        if (!inventRequest.getItemId().equals(inventItemExist.getItem().getId())){
            throw new ErrorException("Cannot Edit ItemId in Inventory", "Bad Request", HttpStatus.BAD_REQUEST);
        }
    }
}
