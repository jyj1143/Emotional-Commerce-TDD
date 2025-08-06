package com.loopers.infrastructure.inventory;

import com.loopers.domain.inventory.InventoryModel;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InventoryJpaRepository extends JpaRepository<InventoryModel, Long> {

    Optional<InventoryModel> findByRefProductSkuId(Long productSkuId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM InventoryModel i WHERE i.refProductSkuId = :productSkuId")
    Optional<InventoryModel> findByRefProductSkuIdWithLock(Long productSkuId);

    Boolean existsByRefProductSkuId(Long productSkuId);

    @Modifying
    @Query("UPDATE InventoryModel i "
        + "SET i.quantity.quantity = i.quantity.quantity + :quantity "
        + "WHERE i.refProductSkuId = :productSkuId ")
    int incrementStock(Long productSkuId, Long quantity);

    @Modifying
    @Query("UPDATE InventoryModel i "
        + "SET i.quantity.quantity = i.quantity.quantity - :quantity "
        + "WHERE i.refProductSkuId = :productSkuId "
        + "AND  i.quantity.quantity  >=  :quantity ")
    int decreaseStock(Long productSkuId, Long quantity);
}
