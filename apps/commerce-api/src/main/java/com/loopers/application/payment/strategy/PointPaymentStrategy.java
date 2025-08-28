package com.loopers.application.payment.strategy;


import com.loopers.application.payment.dto.PaymentResult;
import com.loopers.application.payment.strategy.condition.PointPaymentCondition;
import com.loopers.domain.payment.dto.PaymentCommand;
import com.loopers.domain.payment.dto.PaymentCommand.Fail;
import com.loopers.domain.payment.dto.PaymentCommand.Success;
import com.loopers.domain.payment.dto.PaymentInfo;
import com.loopers.domain.payment.service.PaymentService;
import com.loopers.domain.point.service.PointService;
import com.loopers.domain.point.service.dto.PointCommand.UsePoint;
import com.loopers.support.error.CoreException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PointPaymentStrategy implements PaymentStrategy<PointPaymentCondition> {

    private final PaymentService paymentService;
    private final PointService pointService;

    @Override
    @Transactional
    public PaymentResult pay(PointPaymentCondition condition) {
        PaymentInfo paymentInfo = paymentService.findByRefOrderId(condition.getOrderId());
        // 트랜잭션 키 생성
        String transactionKey = generateTransactionKey(condition.getOrderId());
        try {
            // 결제 트랜잭션 저장
            paymentService.readyPaymentGatewayTransaction(
                new PaymentCommand.ReadyTransaction(
                    paymentInfo.id(),
                    condition.getOrderId(),
                    transactionKey,
                    paymentInfo.paymentStatus(),
                    condition.getAmount(),
                    null, // 포인트 결제는 카드 타입 없음
                    null  // 포인트 결제는 카드 번호 없음
                )
            );
            // 포인트 차감
            pointService.usePoint(new UsePoint(condition.getUserId(), condition.getAmount()));
            //  트랜잭션 상태 완료로 변경
            PaymentInfo success = paymentService.success(new Success(condition.getOrderId(), transactionKey));
            return PaymentResult.from(success);
        } catch (CoreException e) {
            PaymentInfo fail = paymentService.fail(new Fail(condition.getOrderId(), transactionKey, e.getMessage()));
            return PaymentResult.from(fail);
        } catch (Exception e) {
            PaymentInfo fail = paymentService.fail(
                new Fail(condition.getOrderId(), transactionKey, "포인트 결제 중 오류가 발생했습니다: " + e.getMessage()));
            return PaymentResult.from(fail);
        }
    }


    /**
     * 포인트 결제용 트랜잭션 키 생성
     */
    private String generateTransactionKey(Long orderId) {
        return String.format("POINT-%s-%s", orderId, UUID.randomUUID().toString().substring(0, 8));
    }

}
