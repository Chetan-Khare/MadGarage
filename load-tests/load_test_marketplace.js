import http from 'k6/http';
import { check, sleep } from 'k6';

// -------------------------------------------------------------------------
// Marketplace Stress Test: Vehicle Filtering & Part Search
// -------------------------------------------------------------------------

export const options = {
  stages: [
    { duration: '30s', target: 50 },  // Ramp up to 50 users
    { duration: '1m', target: 100 }, // Stress peak at 100 users
    { duration: '30s', target: 0 },   // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<300'], // 95% of requests must complete below 300ms
    http_req_failed: ['rate<0.01'],    // Error rate must be less than 1%
  },
};

const BASE_URL = 'http://localhost:8080/api';
const LOGO_URL = 'http://localhost:8080/images/logos';

export default function () {
  // --- STAGE 1: Vehicle Filtering ---
  
  // 1. Fetch Makes
  let resMakes = http.get(`${BASE_URL}/vehicles/makes`);
  check(resMakes, {
    'makes status is 200': (r) => r.status === 200,
    'makes body is not empty': (r) => r.json().length > 0,
  });

  // 2. Load the Maruti Logo (Simulating UI rendering in search results)
  let resLogo = http.get(`${LOGO_URL}/maruti_v2.png`);
  check(resLogo, { 'logo status is 200': (r) => r.status === 200 });

  sleep(1);

  // 3. Fetch Models for 'Tata'
  let resModels = http.get(`${BASE_URL}/vehicles/models?make=Tata`);
  check(resModels, {
    'models status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // --- STAGE 2: Marketplace Search ---

  // 4. Search for 'Brakes' Category
  let resBrakes = http.get(`${BASE_URL}/products?category=Brakes`);
  check(resBrakes, {
    'search brakes status is 200': (r) => r.status === 200,
  });

  sleep(1);

  // 5. Search for 'Engine' Category
  let resEngine = http.get(`${BASE_URL}/products?category=Engine`);
  check(resEngine, {
    'search engine status is 200': (r) => r.status === 200,
  });

  sleep(2);
}
