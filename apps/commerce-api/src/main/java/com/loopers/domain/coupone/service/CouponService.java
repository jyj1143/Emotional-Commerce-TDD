package com.loopers.domain.coupone.service;


import com.loopers.domain.coupone.dto.CouponCommand;
import com.loopers.domain.coupone.entity.CouponModel;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.enums.CouponStatus;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.domain.coupone.repository.CouponRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import com.loopers.support.pagenation.PageResult;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponPolicyRepository couponPolicyRepository;

    @Transactional
    public CouponModel issueCoupon(CouponCommand.IssueCoupon command) {
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

        // 쿠폰 수량 감소 (한 번의 업데이트로 처리)
        couponPolicyRepository.decreaseRemainQuantity(command.couponPolicyId());

        CouponModel couponModel = CouponModel.of(
            command.couponPolicyId(),
            command.userId(),
            CouponStatus.AVAILABLE,
            now,
            couponPolicyModel.getCouponPeriod().getEndTime()
        );

        return couponRepository.save(couponModel);
    }

    @Transactional
    public CouponModel useCoupon(CouponCommand.UseCoupon command) {
        CouponModel couponModel = couponRepository.findByIdAndUserId(command.couponId(), command.userId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰을 찾을 수 없습니다."));

        couponModel.use(command.orderId());

        return couponModel;
    }

    @Transactional(readOnly = true)
    public PageResult<CouponModel> getCoupons(CouponCommand.GetMyCoupons command) {
        return couponRepository.findUserCoupon(command.userId(), command.page(), command.size());
    }

}
