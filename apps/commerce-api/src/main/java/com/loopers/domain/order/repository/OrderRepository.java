package com.loopers.domain.order.repository;

import com.loopers.domain.order.OrderModel;
import java.util.Optional;

public interface OrderRepository {

    OrderModel save(OrderModel order);

    Optional<OrderModel> find(Long id);
}
