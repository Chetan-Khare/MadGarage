import http from 'k6/http';
import { check, sleep } from 'k6';

// -------------------------------------------------------------------------
// Marketplace Load Test: Vehicle Filtering & Part Search
// -------------------------------------------------------------------------

export const options = {
  stages: [
    { duration: '30s', target: 20 }, // Ramp up to 20 users
    { duration: '1m', target: 20 },  // Stay at 20 users
    { duration: '30s', target: 0 },  // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests must complete below 500ms
    http_req_failed: ['rate<0.01'],    // Error rate must be less than 1%
  },
};

const BASE_URL = 'http://localhost:8080/api';

export default function () {
  // --- STAGE 1: Vehicle Filtering ---
  
  // 1. Fetch Makes
  let resMakes = http.get(`${BASE_URL}/vehicles/makes`);
  check(resMakes, {
    'makes status is 200': (r) => r.status === 200,
    'makes body is not empty': (r) => r.json().length > 0,
  });

  sleep(1);

  // 2. Fetch Models for 'Tata'
  let resModels = http.get(`${BASE_URL}/vehicles/models?make=Tata`);
  check(resModels, {
    'models status is 200': (r) => r.status === 200,
    'models for Tata found': (r) => r.json().includes('Nexon'),
  });

  sleep(1);

  // --- STAGE 2: Marketplace Search ---

  // 3. Search for 'Brakes' Category
  let resBrakes = http.get(`${BASE_URL}/products?category=Brakes`);
  check(resBrakes, {
    'search brakes status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // 4. Search for 'Engine' Category
  let resEngine = http.get(`${BASE_URL}/products?category=Engine`);
  check(resEngine, {
    'search engine status is 200': (r) => r.status === 200,
  });

  sleep(2);
}
