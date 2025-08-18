import http from 'k6/http';
import {check, sleep} from 'k6';

// 부하 테스트 시나리오 설정
export const options = {
  stages: [
    {duration: '10s', target: 1},   // 초기 워밍업
    {duration: '10s', target: 5},   // 점진적 증가
    {duration: '10s', target: 10},  // 중간 강도
    {duration: '10s', target: 20},  // 높은 부하
    {duration: '10s', target: 50},  // 최대 부하
    {duration: '10s', target: 0},   // 부하 완화 (쿨다운)
  ],
};

const BASE_URL = 'http://localhost:8080/api/v1/products';
const SALE_PRICE_SORT_PARAMS = '?page=1&size=20&sortType=SALE_PRICE&sortOrder=ASC';
const LIKE_SORT_PARAMS = '?page=1&size=20&sortType=LIKE&sortOrder=DESC';

// VU(가상 유저) 함수 - 각 가상 유저가 이 함수를 반복 실행
export default function () {
  const res = http.get(`${BASE_URL}${LIKE_SORT_PARAMS}`);

  // 응답 검증
  check(res, {
    'status is 200': (r) => r.status === 200,
    'duration < 2000ms': (r) => r.timings.duration < 2000,
  });

  // 다음 요청 전 대기 (1초)
  sleep(1);
}
