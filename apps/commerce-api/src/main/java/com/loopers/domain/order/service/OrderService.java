package com.loopers.domain.order.service;

import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.dto.OrderCommand;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderInfo placeOrder(OrderCommand.Order command) {
        OrderModel orderModel = OrderModel.of(
            command.userId(),
            command.orderItemModels(),
            OrderStatus.PENDING);
        OrderModel save = orderRepository.save(orderModel);
        return OrderInfo.from(save);
    }

}
