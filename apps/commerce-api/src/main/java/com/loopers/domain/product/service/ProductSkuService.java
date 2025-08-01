package com.loopers.domain.product.service;

import com.loopers.domain.product.dto.sku.ProductSkuCommand.GetProSkus;
import com.loopers.domain.product.dto.sku.ProductSkuCommand.GetSku;
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

    public ProductSkuInfo get(GetSku command) {
        ProductSkuModel productSkuModel = productSkuRepository.find(command.id())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품옵션입니다."));
        return ProductSkuInfo.from(productSkuModel);
    }

    public List<ProductSkuInfo> getSkus(GetProSkus command) {
        List<ProductSkuModel> productSkuModel = productSkuRepository.findAllById(command.ids());
        return productSkuModel.stream().map(
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
