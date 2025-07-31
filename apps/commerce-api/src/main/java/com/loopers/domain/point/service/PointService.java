package com.loopers.domain.point.service;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.point.PointModel;
import com.loopers.domain.point.repository.PointRepository;
import com.loopers.domain.point.service.dto.PointCommand;
import com.loopers.domain.point.service.dto.PointInfo;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;


    @Transactional
    public PointInfo create(PointCommand.Create command) {
        PointModel pointModel = PointModel.of(command.point(), command.userId());
        if (pointRepository.existsByUserId(pointModel.getRefUserId())) {
            throw new CoreException(ErrorType.CONFLICT, "현재 회원의 포인트가 존재합니다.");
        }
        PointModel save = pointRepository.save(pointModel);
        return PointInfo.from(save);
    }

    @Transactional
    public PointInfo chargePoint(PointCommand.ChargePoint command) {
        PointModel pointModel = pointRepository.findByUserId(command.userId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원이 존재하지 않습니다."));

        PointModel update = pointRepository.increase(command.userId(), command.point());
        return PointInfo.from(update);
    }

    @Transactional
    public PointInfo usePoint(PointCommand.UsePoint command) {
        PointModel pointModel = pointRepository.findByUserId(command.userId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원이 존재하지 않습니다."));

        if (pointModel.getAmount().getAmount() < command.point()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "포인트가 부족합니다.");
        }

        PointModel update = pointRepository.decrease(command.userId(), command.point());
        return PointInfo.from(update);
    }

    public PointInfo getPoint(PointCommand.GetPoint command) {
        return pointRepository.findByUserId(command.userId()).map(PointInfo::from)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "회원 포인트가 존재하지 않습니다."));
    }
}
