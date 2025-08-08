package com.loopers.interfaces.coupon;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateRequest;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.CreateResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.GetCouponPolicyResponse;
import com.loopers.interfaces.api.coupon.CouponPolicyV1Dto.SummaryListResponse;
import com.loopers.support.pagenation.PageResult;
import com.loopers.support.pagenation.Pageable;
import com.loopers.utils.DatabaseCleanUp;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CouponPolicyV1ApiE2ETest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/coupon-policy")
    @Nested
    class Create {

        private static final Function<String, String> ENDPOINT = pathVariable -> "/api/v1/coupon-policy" + pathVariable;

        @DisplayName("쿠폰 정책 생성 요청을 하면 , 200 OK 응답을 리턴한다.")
        @Test
        void when_createCouponPolicy_then_returnOk() {
            String requestUrl = ENDPOINT.apply("");
            CreateRequest request = new CreateRequest(

            );

            ParameterizedTypeReference<ApiResponse<CreateResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<CreateResponse>> actual =
                testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(request), responseType);

            assertTrue(actual.getStatusCode().is2xxSuccessful());
        }
    }


    @DisplayName("GET /api/v1/coupon-policy/{couponId}")
    @Nested
    class GetCouponPolicy {

        private static final Function<Long, String> ENDPOINT = id -> "/api/v1/coupon-policy/" + id;

        @DisplayName("쿠폰 정책 조회에 성공하면, 쿠폰 정책 정보를 반환한다.")
        @Test
        void when_createCouponPolicy_then_returnOk() {
            Long request = 1L;
            String requestUrl = ENDPOINT.apply(request);

            ParameterizedTypeReference<ApiResponse<GetCouponPolicyResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<GetCouponPolicyResponse>> actual =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(request), responseType);

            assertTrue(actual.getStatusCode().is2xxSuccessful());
        }
    }

    @DisplayName("GET /api/v1/coupon-policy")
    @Nested
    class GetCouponPolicies {

        private static final Function<String, String> ENDPOINT = pathVariable -> "/api/v1/coupon-policy" + pathVariable;

        @DisplayName("쿠폰 정책을 목록 조회에 성공하면, 쿠폰 정책을 목록 정보를 반환한다.")
        @Test
        void when_createCouponPolicy_then_returnOk() {
            Pageable request = new Pageable();
            String requestUrl = ENDPOINT.apply("");

            ParameterizedTypeReference<ApiResponse<PageResult<SummaryListResponse>>> responseType = new ParameterizedTypeReference<>() {
            };

            ResponseEntity<ApiResponse<PageResult<SummaryListResponse>>> actual =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(request), responseType);

            assertTrue(actual.getStatusCode().is2xxSuccessful());
        }
    }
}
