package com.septian.inventoryapp.repository;

import com.septian.inventoryapp.model.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String> {

    @Query(value = "SELECT COUNT(*) FROM inventory WHERE item_id = :itemId", nativeQuery = true)
    long countItemUsed(@Param("itemId") int itemId);
}
