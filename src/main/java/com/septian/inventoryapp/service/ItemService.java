package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.entity.InventoryEntity;
import com.septian.inventoryapp.model.entity.ItemEntity;
import com.septian.inventoryapp.model.request.SaveItemRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.ItemResponse;
import com.septian.inventoryapp.repository.InventoryReposity;
import com.septian.inventoryapp.repository.ItemRepository;
import com.septian.inventoryapp.repository.OrderRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class ItemService implements IItemService{
    public final ItemRepository itemRepository;
    public final InventoryReposity inventoryReposity;
    public final OrderRepository orderRepository;

    @Autowired
    public ItemService(ItemRepository itemRepository, InventoryReposity inventoryReposity, OrderRepository orderRepository) {
        this.itemRepository = itemRepository;
        this.inventoryReposity = inventoryReposity;
        this.orderRepository = orderRepository;
    }

    @Override
    public BaseResponse<ItemResponse> getById(Integer id) {
        BaseResponse<ItemResponse> response = new BaseResponse<>();
        Optional<ItemEntity> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()){
            ItemEntity item = itemOptional.get();
            ItemResponse itemRes = new ItemResponse(item.getId(), item.getName(), item.getPrice());

            InventoryEntity inventEntity = inventoryReposity.getByItemIdAndType(item.getId(),"T");
            if (!Objects.isNull(inventEntity)){
                itemRes.setQty(inventEntity.getQty());
            }

            response.setContent(itemRes);
            response.setMessage("Success");
        } else {
            throw new ErrorException("Item "+id+" Not Found","Bad Request", HttpStatus.NOT_FOUND);
        }

        return response;
    }

    @Override
    public void saveItem(SaveItemRequest itemRequest) {
        validateRequest(itemRequest,"ADD");
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName(itemRequest.getName());
        itemEntity.setPrice(itemRequest.getPrice());
        itemRepository.save(itemEntity);
    }

    @Override
    public BaseResponse<ItemResponse> updateItem(SaveItemRequest itemRequest) {
        validateRequest(itemRequest,"UPDATE");
        BaseResponse<ItemResponse> res = new BaseResponse<>();
        ItemResponse itemResponse = new ItemResponse();
        Optional<ItemEntity> itemOptional = itemRepository.findById(itemRequest.getId());
        if (itemOptional.isPresent()){
            ItemEntity itemEntity = itemOptional.get();
            itemEntity.setName(StringUtils.isBlank(itemRequest.getName())? itemEntity.getName() : itemRequest.getName());
            itemEntity.setPrice(itemRequest.getPrice()!=null?itemRequest.getPrice():itemEntity.getPrice());
            itemRepository.save(itemEntity);

            itemResponse.setId(itemEntity.getId());
            itemResponse.setName(itemEntity.getName());
            itemResponse.setPrice(itemEntity.getPrice());

            res.setContent(itemResponse);
            res.setMessage("Updated!");
        } else {
            throw new ErrorException("Item "+itemRequest.getId()+" Not Found","Bad Request", HttpStatus.BAD_REQUEST);
        }
        return res;
    }

    @Override
    public void deleteItem(int id) {
        Optional<ItemEntity> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()){
            long itemExistInvent = inventoryReposity.countItemUsed(id);
            long itemExistOrder = orderRepository.countItemUsed(id);
            if (itemExistOrder <=0 && itemExistInvent <= 0) {
                itemRepository.deleteById(id);
            } else {
                throw new ErrorException("Item "+id+" Already Used","Bad Request", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new ErrorException("Item "+id+" Not Found","Bad Request", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public Page<ItemResponse> getAll(Pageable pageable) {
        return itemRepository.findAll(pageable).map(itemEntity -> {
            ItemResponse res = new ItemResponse();
            res.setId(itemEntity.getId());
            res.setName(itemEntity.getName());
            res.setPrice(itemEntity.getPrice());
            InventoryEntity inventEntity = inventoryReposity.getByItemIdAndType(itemEntity.getId(),"T");
            if (!Objects.isNull(inventEntity)){
                res.setQty(inventEntity.getQty());
            }
            return res;
        });
    }

    public void validateRequest(SaveItemRequest itemRequest, String method){
        if (method.equals("UPDATE")) {
            if (itemRequest.getId() == null) {
                throw new ErrorException("id cannot be null", "Bad Request", HttpStatus.BAD_REQUEST);
            }
        }
        if (StringUtils.isBlank(itemRequest.getName())){
            throw new ErrorException("name cannot be null","Bad Request", HttpStatus.BAD_REQUEST);
        }
    }
}
