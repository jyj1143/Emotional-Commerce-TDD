package com.loopers.domain.order.service;

import com.loopers.domain.order.OrderItemModel;
import com.loopers.domain.order.OrderModel;
import com.loopers.domain.order.dto.OrderCommand;
import com.loopers.domain.order.dto.OrderEvent;
import com.loopers.domain.order.dto.OrderGlobalEvent;
import com.loopers.domain.order.dto.OrderInfo;
import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.domain.order.repository.OrderRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final OrderGlobalV1EventPublisher orderGlobalV1EventPublisher;

    @Transactional
    public OrderInfo placeOrder(OrderCommand.Order command) {
        List<OrderItemModel> orderItemModels = command.orderItem().stream()
            .map(item -> OrderItemModel.of(item.quantity(), item.purchasePrice(), item.refProductSkuId()))
            .toList();

        OrderModel orderModel = OrderModel.of(
            command.userId(),
            orderItemModels,
            OrderStatus.PENDING);
        OrderModel save = orderRepository.save(orderModel);

        return OrderInfo.from(save);
    }

    @Transactional
    public OrderInfo pendingPayment(OrderCommand.PendingPayment command) {
        OrderModel orderModel = orderRepository.find(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));
        orderModel.pendingPayment(command.finalPrice());

        eventPublisher.publish(OrderEvent.Created.from(orderModel, command.couponId()));
        return OrderInfo.from(orderModel);
    }

    @Transactional
    public OrderInfo completeOrder(OrderCommand.Complete command) {
        OrderModel orderModel = orderRepository.find(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));
        orderModel.completeOrder();
        orderGlobalV1EventPublisher.publish(OrderGlobalEvent.Order.from(orderModel));
        return OrderInfo.from(orderModel);
    }

    @Transactional
    public OrderInfo cancelOrder(OrderCommand.Cancel command) {
        OrderModel orderModel = orderRepository.find(command.orderId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "주문을 찾을 수 없습니다."));
        orderModel.canceled();
        eventPublisher.publish(OrderEvent.Cancelled.from(orderModel, command.couponId()));
        return OrderInfo.from(orderModel);
    }

}
