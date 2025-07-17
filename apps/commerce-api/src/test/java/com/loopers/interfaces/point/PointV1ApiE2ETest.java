package com.loopers.interfaces.point;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.loopers.domain.user.BirthDate;
import com.loopers.domain.user.Email;
import com.loopers.domain.user.Gender;
import com.loopers.domain.user.LoginInfo;
import com.loopers.domain.user.UserModel;
import com.loopers.domain.user.UserService;
import com.loopers.domain.user.dto.UserCommand;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.point.PointV1Dto;
import com.loopers.interfaces.api.point.PointV1Dto.PointResponse;
import com.loopers.interfaces.api.user.UserV1Dto;
import com.loopers.interfaces.api.user.UserV1Dto.SignUpResponse;
import com.loopers.utils.DatabaseCleanUp;
import jakarta.persistence.EntityManager;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PointV1ApiE2ETest {

    private static final Function<String, String> ENDPOINT = pathVariable -> "/api/v1/points" + pathVariable;
    @Autowired
    private TestRestTemplate testRestTemplate;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;
    @Autowired
    private UserService userService;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("GET /api/v1/points")
    @Nested
    class GetPoint {

        @DisplayName("포인트 조회에 성공할 경우, 보유 포인트를 응답으로 반환한다.")
        @Test
        void whenRequestPointSuccess_thenReturnMyPoint() {
            // given
            LoginInfo loginInfo = new LoginInfo("test");
            Email email = new Email("test@gmail.com");
            Gender male = Gender.MALE;
            BirthDate birthDate = new BirthDate("1997-02-27");
            UserCommand.Create user = new UserCommand.Create(
                loginInfo,
                email,
                male,
                birthDate
            );
            userService.signUp(user);
            userService.addPoint(loginInfo, 1000L);

            String requestUrl = ENDPOINT.apply("");
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", loginInfo.getLoginId());
            ParameterizedTypeReference<ApiResponse<PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            // when
            ResponseEntity<ApiResponse<PointResponse>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(headers), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody()).isNotNull(),
                () -> assertThat(response.getBody().data()).isNotNull(),
                () -> assertThat(response.getBody().data().amount()).isEqualTo(1000L)
            );
        }


        @DisplayName("X-USER-ID 헤더가 없을 경우, 400 Bad Request 응답을 반환한다.")
        @Test
        void whenRequestWithoutHeader_thenReturn400Error() {
            // given
            String requestUrl = ENDPOINT.apply("");
            ParameterizedTypeReference<ApiResponse<PointResponse>> responseType = new ParameterizedTypeReference<>() {
            };

            // when
            ResponseEntity<ApiResponse<PointResponse>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, new HttpEntity<>(null), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST)
            );
        }
    }

    @DisplayName("POST /api/v1/points")
    @Nested
    class ChargePoint {

        @DisplayName("존재하는 유저가 1000원을 충전할 경우, 충전된 보유 총량을 응답으로 반환한다.")
        @Test
        void givenExistUser_whenChargePoint_thenReturnChargedPoint(){
            // given
            String requestUrl = ENDPOINT.apply("");
            LoginInfo loginInfo = new LoginInfo("user");
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", loginInfo.getLoginId());
            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(loginInfo.getLoginId(), 1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.ChargeRequest>> responseType = new ParameterizedTypeReference<>() {
            };

            // when
            ResponseEntity<ApiResponse<PointV1Dto.ChargeRequest>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(request, headers), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is2xxSuccessful()),
                () -> assertThat(response.getBody().data().amount()).isEqualTo(1000L)
            );

        }

        @DisplayName("존재하지 않는 유저로 요청할 경우, 404 Not Found 응답을 반환한다.")
        @Test
        void givenNotExistUser_whenChargePoint_thenReturnNotFoundError() {
            // given
            String requestUrl = ENDPOINT.apply("");
            LoginInfo loginInfo = new LoginInfo("test");
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-USER-ID", loginInfo.getLoginId());
            PointV1Dto.ChargeRequest request = new PointV1Dto.ChargeRequest(loginInfo.getLoginId(), 1000L);
            ParameterizedTypeReference<ApiResponse<PointV1Dto.ChargeRequest>> responseType = new ParameterizedTypeReference<>() {
            };

            // when
            ResponseEntity<ApiResponse<PointV1Dto.ChargeRequest>> response =
                testRestTemplate.exchange(requestUrl, HttpMethod.POST, new HttpEntity<>(request, headers), responseType);

            // then
            assertAll(
                () -> assertTrue(response.getStatusCode().is4xxClientError()),
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND)
            );
        }

    }
}
