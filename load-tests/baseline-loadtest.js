import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 }, // Ramp up to 50 users
    { duration: '1m', target: 50 },  // Stay at 50 users
    { duration: '30s', target: 0 },  // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<200'], // 95% of requests must be under 200ms
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // 1. Fetch Public Config (Now Cached)
  const configRes = http.get(`${BASE_URL}/config/public`);
  check(configRes, { 'config status is 200': (r) => r.status === 200 });

  // 2. Fetch Vehicle Makes (Highly cached in frontend, but we test backend latency)
  const makesRes = http.get(`${BASE_URL}/vehicles/makes`);
  check(makesRes, { 'makes status is 200': (r) => r.status === 200 });

  // 3. Simulated think time
  sleep(1);
}
