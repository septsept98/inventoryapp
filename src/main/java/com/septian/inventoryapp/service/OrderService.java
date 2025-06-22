package com.septian.inventoryapp.service;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.entity.InventoryEntity;
import com.septian.inventoryapp.model.entity.ItemEntity;
import com.septian.inventoryapp.model.entity.OrderEntity;
import com.septian.inventoryapp.model.request.SaveInventRequest;
import com.septian.inventoryapp.model.request.SaveOrderRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.OrderResponse;
import com.septian.inventoryapp.repository.InventoryReposity;
import com.septian.inventoryapp.repository.ItemRepository;
import com.septian.inventoryapp.repository.OrderRepository;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService implements IOrderService{
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    InventoryReposity inventoryReposity;

    public void validateRequest(SaveOrderRequest request){
        if (StringUtils.isBlank(request.getOrderId())){
            throw new ErrorException("OrderId Cannot Null","Bad Request");
        }
        if (request.getItemId() == null){
            throw new ErrorException("ItemId Cannot Null","Bad Request");
        }
        if (request.getQty()<=0){
            throw new ErrorException("The Quantity Must be Greater Than 0", "Bad Request");
        }
    }

    @Override
    public void saveOrder(SaveOrderRequest request) {
        validateRequest(request);
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(request.getOrderId());
        if (orderEntityOptional.isEmpty()){
            Optional<ItemEntity> itemEntityOptional = itemRepository.findById(request.getItemId());
            if (itemEntityOptional.isPresent()){
                List<InventoryEntity> inventList = inventoryReposity.getByItemId(request.getItemId());
                if (inventList.isEmpty()){
                    throw new ErrorException("Inventory Item Not Found", "Bad Request");
                }
                InventoryEntity inventExistT = inventList.stream()
                        .filter(invent -> invent.getType().equalsIgnoreCase("T"))
                        .findFirst()
                        .orElse(null);
                InventoryEntity inventExistW = inventList.stream()
                        .filter(invent -> invent.getType().equalsIgnoreCase("W"))
                        .findFirst()
                        .orElse(null);

                if (!ObjectUtils.isEmpty(inventExistT)){
                    if (inventExistT.getQty() >= request.getQty()){
                        inventExistT.setQty(inventExistT.getQty()- request.getQty());
                        inventoryReposity.save(inventExistT);
                    } else {
                        throw new ErrorException("The Item Stock is Insufficient","Bad Request");
                    }
                } else {
                    throw new ErrorException("Top Up Inventory First","Bad Request");
                }

                if (!ObjectUtils.isEmpty(inventExistW)){
                    inventExistW.setQty(inventExistW.getQty() + request.getQty());
                    inventoryReposity.save(inventExistW);
                } else {
                    InventoryEntity inventoryEntityW = new InventoryEntity();
                    inventoryEntityW.setItem(itemEntityOptional.get());
                    inventoryEntityW.setQty(request.getQty());
                    inventoryEntityW.setType("W");
                    inventoryReposity.save(inventoryEntityW);
                }

                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderNo(request.getOrderId());
                orderEntity.setQty(request.getQty());
                orderEntity.setItem(itemEntityOptional.get());
                orderEntity.setPrice(itemEntityOptional.get().getPrice().multiply(BigDecimal.valueOf(request.getQty())));
                orderRepository.save(orderEntity);
            } else {
                throw new ErrorException("Item Not Found", "Bad Request");
            }
        } else {
            throw new ErrorException("OrderId Already Used","Bad Request");
        }
    }

    @Override
    public void updateOrder(SaveOrderRequest request) {
        validateRequest(request);
        Optional<OrderEntity> orderEntityOptional = orderRepository.findById(request.getOrderId());
        if (orderEntityOptional.isPresent()){
            OrderEntity orderEntityExist = orderEntityOptional.get();
            if (orderEntityExist.getItem().getId() == request.getItemId()) {
                List<InventoryEntity> inventList = inventoryReposity.getByItemId(request.getItemId());

                if (inventList.isEmpty())
                    throw new ErrorException("Inventory Item Not Found", "Bad Request");

                InventoryEntity inventExistT = inventList.stream()
                        .filter(invent -> invent.getType().equalsIgnoreCase("T"))
                        .findFirst()
                        .orElse(null);

                InventoryEntity inventExistW = inventList.stream()
                        .filter(invent -> invent.getType().equalsIgnoreCase("W"))
                        .findFirst()
                        .orElse(null);

                if (request.getQty() > orderEntityExist.getQty()){
                    int difQty = request.getQty() - orderEntityExist.getQty();
                    if (!ObjectUtils.isEmpty(inventExistT)){
                        if (inventExistT.getQty() >= difQty){
                            inventExistT.setQty(inventExistT.getQty() - difQty);
                            inventoryReposity.save(inventExistT);
                        } else
                            throw new ErrorException("The Item Stock is Insufficient","Bad Request");
                    } else {
                        throw new ErrorException("Top Up Inventory First","Bad Request");
                    }
                    if (!ObjectUtils.isEmpty(inventExistW)){
                        inventExistW.setQty(inventExistW.getQty() + difQty);
                        inventoryReposity.save(inventExistW);
                    }
                }
                if (request.getQty() < orderEntityExist.getQty()){
                    int difQty = orderEntityExist.getQty() - request.getQty();
                    if (!ObjectUtils.isEmpty(inventExistT)){
                        inventExistT.setQty(inventExistT.getQty() + difQty);
                        inventoryReposity.save(inventExistT);
                    } else {
                        throw new ErrorException("Top Up Inventory First","Bad Request");
                    }
                    if (!ObjectUtils.isEmpty(inventExistW)){
                        inventExistW.setQty(inventExistW.getQty() - difQty);
                        inventoryReposity.save(inventExistW);
                    }
                }
                orderEntityExist.setQty(request.getQty());
                orderEntityExist.setPrice(inventExistT.getItem().getPrice().multiply(BigDecimal.valueOf(request.getQty())));
                orderRepository.save(orderEntityExist);
            } else {
                throw new ErrorException("Cannot Change Item", "Bad Request");
            }
        } else {
            throw new ErrorException("OrderId Not Found","Bad Request");
        }
    }

    @Override
    public void deleteById(String id) {
        Optional<OrderEntity> orderEntity = orderRepository.findById(id);
        if (orderEntity.isPresent()){
            orderRepository.deleteById(id);
        } else {
            throw new ErrorException("Order Not Found", "Bad Request");
        }
    }

    @Override
    public BaseResponse<OrderResponse> getByIdOrder(String orderId) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(orderId);
        OrderResponse response = new OrderResponse();
        if (orderOptional.isPresent()) {
            Optional<ItemEntity> item = itemRepository.findById(orderOptional.get().getItem().getId());
            if (item.isPresent()) {
                ItemEntity itemEntity = item.get();
                response.setOrderId(orderOptional.get().getOrderNo());
                response.setItemId(itemEntity.getId());
                response.setItemName(itemEntity.getName());
                response.setItemPrice(itemEntity.getPrice());
                response.setQty(orderOptional.get().getQty());
                response.setPrice(orderOptional.get().getPrice());
            } else {
                throw new ErrorException("Item Not Found", "Bad Request");
            }
        } else {
            throw new ErrorException("Order Not Found", "Bad Request");
        }
        return new BaseResponse<>("Success",response);
    }

    @Override
    public Page<OrderResponse> getAllOrder(Pageable pageable) {
        Page<OrderEntity> listOrderEnt = orderRepository.findAll(pageable);
        return listOrderEnt.map(entity -> {
              OrderResponse res = new OrderResponse();
              res.setOrderId(entity.getOrderNo());
              res.setItemId(entity.getItem().getId());
              res.setItemName(entity.getItem().getName());
              res.setItemPrice(entity.getItem().getPrice());
              res.setQty(entity.getQty());
              res.setPrice(entity.getPrice());
              return res;
          });
    }
}
