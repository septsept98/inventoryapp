package com.septian.inventoryapp.repository;

import com.septian.inventoryapp.model.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryReposity extends JpaRepository<InventoryEntity, Integer> {
    InventoryEntity getByItemIdAndType(Integer itemId, String type);
    List<InventoryEntity> getByItemId(Integer itemId);

    @Query(value = "SELECT COUNT(*) FROM inventory WHERE item_id = :itemId", nativeQuery = true)
    long countItemUsed(@Param("itemId") int itemId);
}
