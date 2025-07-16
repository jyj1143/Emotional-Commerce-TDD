package com.loopers.interfaces.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.example.ExampleV1Dto;
import com.loopers.interfaces.api.example.ExampleV1Dto.ExampleResponse;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.function.Function;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserV1ApiE2ETest {

    private static final Function<String, String> ENDPOINT = pathVariable -> "/api/v1/users" + pathVariable;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    class Join {

        @DisplayName("회원 가입이 성공할 경우, 생성된 유저 정보를 응답으로 반환한다.")
        @Test
        void whenJoinSuccess_thenReturnUserInfo() {
            // given
            String requestUrl = ENDPOINT.apply("");
            UserV1Dto.SignUpRequest request = new UserV1Dto.SignUpRequest(
                "test",
                "MALE",
                "1997-02-27",
                "test@example.com"
            );

            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.SignUpResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserV1Dto.SignUpResponse>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(request), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertNotNull(response.getBody()),
                () -> assertNotNull(response.getBody().data()),
                () -> assertThat(response.getBody().data().loginId()).isEqualTo(request.loginId()),
                () -> assertThat(response.getBody().data().gender()).isEqualTo(request.gender()),
                () -> assertThat(response.getBody().data().birthDate()).isEqualTo(request.birthDate()),
                () -> assertThat(response.getBody().data().email()).isEqualTo(request.email())
            );
        }

        @DisplayName("회원 가입 시에 성별이 없을 경우, `400 Bad Request` 응답을 반환한다.")
        @Test
        void returnsBadRequest_whenGenderIsMissing() {
            // given
            String requestUrl = ENDPOINT.apply("");
            UserV1Dto.SignUpRequest request = new UserV1Dto.SignUpRequest(
                "test",
                "",
                "1997-02-27",
                "test@example.com"
            );
            // when
            ParameterizedTypeReference<ApiResponse<UserV1Dto.SignUpResponse>> responseType = new ParameterizedTypeReference<>() {
            };
            ResponseEntity<ApiResponse<UserV1Dto.SignUpResponse>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(request), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }

    }

}
