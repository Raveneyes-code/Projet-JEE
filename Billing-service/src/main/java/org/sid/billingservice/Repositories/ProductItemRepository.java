package org.sid.billingservice.Repositories;

import org.sid.billingservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface ProductItemRepository extends JpaRepository<ProductItem,Long> {
    public Collection<ProductItem> findByBillId(Long id);
}
