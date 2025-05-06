import http from 'k6/http';
import { check } from 'k6';

export const options = {
  stages: [
    { duration: '5s', target: 20 }, // ramp up to 20 users over 5 seconds
    { duration: '10s', target: 20 }, // stay at 100 users for 20 seconds
    { duration: '5s', target: 0 }, // ramp down to 0 users over 5 seconds
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'], // http errors should be less than 1%
    http_req_duration: ['p(95)<500'], // 95% of requests should be below 500ms
  },
};

export default function () {
  // target url  should be obtained from environment variable TARGET_URL or else should be https://tutorial-spring-staging.onrender.com
  let target_url = __ENV.K6_API_URL || 'https://tutorial-spring-staging.onrender.com';
  const res = http.get(target_url);

  check(res, {
    'status is 200': (r) => r.status === 200,
  });
}