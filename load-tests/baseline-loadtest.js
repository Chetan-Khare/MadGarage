import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '30s', target: 50 }, // Ramp up to 50 users
    { duration: '1m', target: 100 }, // Increase to 100 users for stress test
    { duration: '30s', target: 0 },  // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<250'], // 95% of requests must be under 250ms
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // 1. Fetch Public Config
  const configRes = http.get(`${BASE_URL}/config/public`);
  check(configRes, { 'config status is 200': (r) => r.status === 200 });

  // 2. Fetch Vehicle Makes
  const makesRes = http.get(`${BASE_URL}/vehicles/makes`);
  check(makesRes, { 'makes status is 200': (r) => r.status === 200 });

  // 3. Fetch a sample localized logo (Verifying static resource handler performance)
  const logoRes = http.get(`http://localhost:8080/images/logos/maruti_v2.png`);
  check(logoRes, { 'logo status is 200': (r) => r.status === 200 });

  // 4. Simulated think time
  sleep(1);
}
