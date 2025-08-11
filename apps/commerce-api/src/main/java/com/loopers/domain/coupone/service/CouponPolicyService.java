package com.loopers.domain.coupone.service;


import com.loopers.domain.coupone.dto.CouponPolicyCommand;
import com.loopers.domain.coupone.entity.CouponPolicyModel;
import com.loopers.domain.coupone.repository.CouponPolicyRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponPolicyService {

    private final CouponPolicyRepository couponPolicyRepository;

    @Transactional
    public CouponPolicyModel create(CouponPolicyCommand.Create command) {
        CouponPolicyModel policyModel = command.toEntity();
        return couponPolicyRepository.save(policyModel);
    }

    @Transactional(readOnly = true)
    public CouponPolicyModel getCouponPolicy(Long id) {
        return couponPolicyRepository.find(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "쿠폰 정책을 찾을 수 없습니다."));
    }
}
