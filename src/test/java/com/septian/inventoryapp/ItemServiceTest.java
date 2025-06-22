package com.septian.inventoryapp;

import com.septian.inventoryapp.model.dto.ErrorException;
import com.septian.inventoryapp.model.entity.InventoryEntity;
import com.septian.inventoryapp.model.entity.ItemEntity;
import com.septian.inventoryapp.model.request.SaveItemRequest;
import com.septian.inventoryapp.model.response.BaseResponse;
import com.septian.inventoryapp.model.response.ItemResponse;
import com.septian.inventoryapp.repository.InventoryReposity;
import com.septian.inventoryapp.repository.ItemRepository;
import com.septian.inventoryapp.repository.OrderRepository;
import com.septian.inventoryapp.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryReposity inventoryReposity;

    @Mock
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetById_success() {
        ItemEntity item = new ItemEntity();
        item.setId(1);
        item.setName("Pensil");
        item.setPrice(BigDecimal.valueOf(3000));

        InventoryEntity inventory = new InventoryEntity();
        inventory.setQty(100);

        Mockito.when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Mockito.when(inventoryReposity.getByItemIdAndType(1, "T")).thenReturn(inventory);

        BaseResponse<ItemResponse> response = itemService.getById(1);

        Assertions.assertEquals("Success", response.getMessage());
        Assertions.assertNotNull(response.getContent());
        Assertions.assertEquals(1, response.getContent().getId());
        Assertions.assertEquals("Pensil", response.getContent().getName());
        Assertions.assertEquals(100, response.getContent().getQty());
    }

    @Test
    void testGetById_notFound() {
        Mockito.when(itemRepository.findById(2)).thenReturn(Optional.empty());

        ErrorException exception = Assertions.assertThrows(ErrorException.class, () -> itemService.getById(2));

        Assertions.assertEquals("Item 2 Not Found", exception.getMessage());
    }

    @Test
    void testSaveItem_success() {
        SaveItemRequest req = new SaveItemRequest();
        req.setName("Pulpen");
        req.setPrice(BigDecimal.valueOf(5000));

        itemService.saveItem(req);

        Mockito.verify(itemRepository).save(Mockito.any(ItemEntity.class));
    }

    @Test
    void testSaveItem_validationFail() {
        SaveItemRequest req = new SaveItemRequest();
        req.setName("");

        ErrorException exception = Assertions.assertThrows(ErrorException.class, () -> itemService.saveItem(req));

        Assertions.assertEquals("name cannot be null", exception.getMessage());
    }

    @Test
    void testUpdateItem_success() {
        ItemEntity item = new ItemEntity();
        item.setId(1);
        item.setName("Pulpen");
        item.setPrice(BigDecimal.valueOf(4000));

        SaveItemRequest req = new SaveItemRequest();
        req.setId(1);
        req.setName("Pulpen Baru");
        req.setPrice(BigDecimal.valueOf(4500));

        Mockito.when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        BaseResponse<ItemResponse> response = itemService.updateItem(req);

        Assertions.assertEquals("Updated!", response.getMessage());
        Assertions.assertEquals("Pulpen Baru", response.getContent().getName());
        Assertions.assertEquals(BigDecimal.valueOf(4500), response.getContent().getPrice());
    }

    @Test
    void testUpdateItem_notFound() {
        SaveItemRequest req = new SaveItemRequest();
        req.setId(99);
        req.setName("Doesn't Matter");

        Mockito.when(itemRepository.findById(99)).thenReturn(Optional.empty());

        ErrorException ex = Assertions.assertThrows(ErrorException.class, () -> itemService.updateItem(req));

        Assertions.assertEquals("Item 99 Not Found", ex.getMessage());
    }

    @Test
    void testDeleteItem_success() {
        ItemEntity item = new ItemEntity();
        item.setId(1);

        Mockito.when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        Mockito.when(inventoryReposity.countItemUsed(1)).thenReturn(0L);
        Mockito.when(orderRepository.countItemUsed(1)).thenReturn(0L);

        itemService.deleteItem(1);

        Mockito.verify(itemRepository).deleteById(1);
    }

    @Test
    void testDeleteItem_alreadyUsed() {
        ItemEntity item = new ItemEntity();
        item.setId(2);

        Mockito.when(itemRepository.findById(2)).thenReturn(Optional.of(item));
        Mockito.when(inventoryReposity.countItemUsed(2)).thenReturn(1L);
        Mockito.when(orderRepository.countItemUsed(2)).thenReturn(0L);

        ErrorException ex = Assertions.assertThrows(ErrorException.class, () -> itemService.deleteItem(2));

        Assertions.assertEquals("Item 2 Already Used", ex.getMessage());
    }

    @Test
    void testDeleteItem_notFound() {
        Mockito.when(itemRepository.findById(3)).thenReturn(Optional.empty());

        ErrorException ex = Assertions.assertThrows(ErrorException.class, () -> itemService.deleteItem(3));

        Assertions.assertEquals("Item 3 Not Found", ex.getMessage());
    }

    @Test
    void testGetAll_success() {
        ItemEntity item = new ItemEntity();
        item.setId(1);
        item.setName("Pulpen");
        item.setPrice(BigDecimal.valueOf(3000));

        InventoryEntity inventory = new InventoryEntity();
        inventory.setQty(20);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemEntity> page = new PageImpl<>(Collections.singletonList(item));

        Mockito.when(itemRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(inventoryReposity.getByItemIdAndType(1, "T")).thenReturn(inventory);

        Page<ItemResponse> result = itemService.getAll(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Pulpen", result.getContent().get(0).getName());
        Assertions.assertEquals(20, result.getContent().get(0).getQty());
    }
}
