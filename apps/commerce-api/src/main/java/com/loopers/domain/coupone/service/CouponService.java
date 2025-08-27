package com.loopers.domain.coupone.service;

import com.loopers.domain.coupone.DiscountPolicy;
import com.loopers.domain.coupone.DiscountPolicyFactory;
import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.dto.CouponDisCountInfo;
import com.loopers.domain.coupone.dto.CouponInfo;
import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.support.pagenation.PageResult;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    @Transactional
    public CouponInfo issueCoupon(CouponCommand.IssueCoupon command) {
        // 쿠폰 정책을 조회하고 락을 획득
        CouponPolicyModel couponPolicyModel = couponPolicyRepository.findWithLock(command.couponPolicyId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다."));
        LocalDateTime now = LocalDateTime.now();

        if (couponPolicyModel.getRemainQuantity().getQuantity() <= 0) {
            throw new CoreException(ErrorType.NOT_FOUND, "쿠폰이 모두 소진되었습니다.");
        }
        if (now.isBefore(couponPolicyModel.getCouponPeriod().getStartTime()) || now.isAfter(
            couponPolicyModel.getCouponPeriod().getEndTime())) {
            throw new CoreException(ErrorType.BAD_REQUEST, "쿠폰 발급 기간이 아닙니다.");
        }

        // 쿠폰 수량 감소 (원자적 업데이트)
        int updated = couponPolicyRepository.decreaseRemainQuantity(command.couponPolicyId());
        if (updated == 0) {
            throw new CoreException(ErrorType.NOT_FOUND, "쿠폰이 모두 소진되었습니다.");
        }

        CouponModel couponModel = CouponModel.of(
            command.couponPolicyId(),
            command.userId(),
            CouponStatus.AVAILABLE,
            now,
            couponPolicyModel.getCouponPeriod().getEndTime()
        );

        CouponModel saved = couponRepository.save(couponModel);
        return CouponInfo.from(saved);
    }

    @Transactional
    public CouponDisCountInfo apply(CouponCommand.Apply command) {
        Long totalPrice = command.totalPrice();
        Long finalPrice = totalPrice;

        // 쿠폰 적용
        if (command.couponId() != null) {
            Long discountAmount = calculateDiscount(command.couponId(), command.userId(), totalPrice);
            finalPrice = Math.max(0, totalPrice - discountAmount);

            // 쿠폰 사용 처리
            useCoupon(new CouponCommand.UseCoupon(command.couponId(), command.orderId(), command.userId()));
        }
        return CouponDisCountInfo.from(command.couponId(), finalPrice);
    }

    @Transactional
    public CouponInfo useCoupon(CouponCommand.UseCoupon command) {
        CouponModel couponModel = couponRepository.findWithLockByIdAndRefUserId(command.couponId(), command.userId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponModel.use(command.orderId());

        return CouponInfo.from(couponModel);
    }

    @Transactional(readOnly = true)
    public PageResult<CouponInfo> getCoupons(CouponCommand.GetMyCoupons command) {
        PageResult<CouponModel> pageResult = couponRepository.findUserCoupon(command.userId(), command.page(), command.size());
        List<CouponInfo> couponInfos = pageResult.content().stream().map(CouponInfo::from).toList();
        return new PageResult<>(
            couponInfos,
            pageResult.paginationInfo()
        );
    }

    @Transactional
    public Long calculateDiscount(Long couponId, Long userId, Long orderAmount) {
        CouponModel couponModel = couponRepository.findByIdAndUserId(couponId, userId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        if (couponModel.isUsed() || couponModel.isExpired()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용할 수 없는 쿠폰입니다.");
        }

        CouponPolicyModel policyModel = couponPolicyRepository.find(couponModel.getRefCouponPolicyId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다."));

        // 최소 주문 금액 검증
        if (policyModel.getOrderAmountCondition().getMinimumOrderAmount() != null
            && orderAmount < policyModel.getOrderAmountCondition().getMinimumOrderAmount()) {
            throw new CoreException(ErrorType.BAD_REQUEST,
                "최소 주문 금액(" + policyModel.getOrderAmountCondition().getMinimumOrderAmount() + "원)을 충족하지 않습니다.");
        }

        // 할인 정책 생성 및 적용
        DiscountPolicy discountPolicy = DiscountPolicyFactory.createFrom(policyModel);
        return discountPolicy.calculateDiscount(orderAmount);
    }

}
