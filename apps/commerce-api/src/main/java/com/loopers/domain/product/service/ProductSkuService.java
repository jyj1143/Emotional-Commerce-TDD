package com.loopers.domain.product.service;

import com.loopers.domain.inventory.InventoryModel;
import com.loopers.domain.inventory.repository.InventoryRepository;
import com.loopers.domain.inventory.service.InventorySkuValidator;
import com.loopers.domain.product.dto.sku.ProductSkuCommand.GetValidSku;
import com.loopers.domain.product.dto.sku.ProductSkuCommand.GetSku;
import com.loopers.domain.product.dto.sku.ProductSkuCommand.OrderSku;
import com.loopers.domain.product.dto.sku.ProductSkuInfo;
import com.loopers.domain.product.entity.ProductSkuModel;
import com.loopers.domain.product.repository.ProductSkuRepository;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSkuService {

    private final ProductSkuRepository productSkuRepository;
    private final InventorySkuValidator inventorySkuValidator;


    public ProductSkuInfo get(GetSku command) {
        ProductSkuModel productSkuModel = productSkuRepository.find(command.id())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품옵션입니다."));
        return ProductSkuInfo.from(productSkuModel);
    }

    public List<ProductSkuInfo> getValidSku(GetValidSku command) {
        // SKU ID 목록 추출
        List<Long> skuIds = command.skuList().stream()
            .map(OrderSku::skuId)
            .toList();

        // SKU 정보 조회
        List<ProductSkuModel> productSkuModels = productSkuRepository.findAllById(skuIds);

        // 재고 수량 확인
        inventorySkuValidator.validateSkuStock(command.skuList());

        return productSkuModels.stream().map(
            ProductSkuInfo::from
        ).toList();
    }

    public ProductSkuModel save(ProductSkuModel productSku) {
        return productSkuRepository.save(productSku);
    }

    public ProductSkuModel saveAll(ProductSkuModel productSku) {
        return productSkuRepository.save(productSku);
    }

}
