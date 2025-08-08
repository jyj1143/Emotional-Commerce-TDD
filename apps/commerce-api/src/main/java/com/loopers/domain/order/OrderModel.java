package com.loopers.domain.order;


import com.loopers.domain.BaseEntity;
import com.loopers.domain.order.enums.OrderStatus;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "orders")
public class OrderModel extends BaseEntity {

    @Column(name = "ref_user_id", nullable = false)
    private Long refUserId;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemModel> orderItemModels = new ArrayList<>();

    private OrderModel(Long refUserId, List<OrderItemModel> orderItemModels, OrderStatus status) {
        if (status == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문상태는 필수 값입니다.");
        }
        if (orderItemModels == null || orderItemModels.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 상품은 최소 1개 이상 필요합니다.");
        }
        if (refUserId == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "유효하지 않은 사용자 ID입니다.");
        }

        this.refUserId = refUserId;
        orderItemModels.forEach(this::addOrderItem);
        this.status = status;
    }

    public static OrderModel of(Long refUserId, List<OrderItemModel> orderItemModels, OrderStatus status) {
        return new OrderModel(refUserId, orderItemModels, status);
    }

    public void addOrderItem(OrderItemModel item) {
        orderItemModels.add(item);
        item.assignToOrderModel(this);
    }

    public Long calculateTotalPrice() {
        return orderItemModels.stream()
            .map(OrderItemModel::calculateTotalPrice)
            .reduce(0L, Long::sum );
    }

    public void completeOrder() {
        this.status = OrderStatus.ORDER_SUCCESS;
    }
}
