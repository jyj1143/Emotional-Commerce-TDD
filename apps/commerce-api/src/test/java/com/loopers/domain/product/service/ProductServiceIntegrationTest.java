package com.loopers.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.loopers.domain.brand.entity.BrandModel;
import com.loopers.domain.brand.repository.BrandRepository;
import com.loopers.domain.product.entity.ProductFixture;
import com.loopers.domain.product.entity.ProductModel;
import com.loopers.domain.product.repository.ProductRepository;
import com.loopers.utils.DatabaseCleanUp;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }


    @DisplayName("상품조회 시,")
    @Nested
    class FindProductWithBrand {

        String brandName = "나이키";

        @BeforeEach
        void setUp() {
            BrandModel brandModel = brandRepository.save(BrandModel.of(brandName));
            Long brandId = brandModel.getId();
            List<ProductModel> productList = ProductFixture.createProductList(brandId);
            productRepository.saveAll(productList);
        }

    }
}
