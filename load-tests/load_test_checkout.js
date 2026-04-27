import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  vus: 20, // Testing 20 concurrent buyers
  iterations: 20,
  thresholds: {
    http_req_failed: ['rate<0.05'],
  },
};

const BASE_URL = 'http://localhost:8080/api';
const TEST_EMAIL = `stress_test_${Date.now()}@madgarage.com`;
const TEST_PASS = 'MadGarage@2026_Secure';

export function setup() {
  // 1. REGISTER & LOGIN
  http.post(`${BASE_URL}/auth/register`, JSON.stringify({
    firstName: 'Load', lastName: 'Tester', email: TEST_EMAIL, password: TEST_PASS
  }), { headers: { 'Content-Type': 'application/json' } });

  const loginRes = http.post(`${BASE_URL}/auth/login`, JSON.stringify({
    email: TEST_EMAIL, password: TEST_PASS
  }), { headers: { 'Content-Type': 'application/json' } });

  const authCookie = loginRes.cookies.mg_auth[0].value;

  // 2. DYNAMICALLY FIND A REAL PRODUCT ID
  const productRes = http.get(`${BASE_URL}/products`);
  const products = JSON.parse(productRes.body);

  if (!products || products.length === 0) {
    throw new Error("DATABASE EMPTY: No products found to test checkout!");
  }

  const liveProductId = products[0].id;
  const liveProductName = products[0].partName;

  console.log(`--- Load Test Targeted at Product [ID: ${liveProductId}] Name: ${liveProductName} ---`);

  return { authCookie, liveProductId };
}

export default function (data) {
  const checkoutParams = {
    headers: { 'Content-Type': 'application/json', 'Cookie': `mg_auth=${data.authCookie}` },
  };

  const res = http.post(`${BASE_URL}/orders/checkout`, JSON.stringify({
    items: [{ productId: data.liveProductId, quantity: 1 }],
    shippingAddress: 'Stress Test Site',
    city: 'Mumbai', state: 'MH', pincode: '400001',
    deliveryType: 'HOME_DELIVERY'
  }), checkoutParams);

  check(res, {
    'checkout successful': (r) => r.status === 200 || r.status === 201,
  });

  if (res.status !== 200 && res.status !== 201) {
    console.error(`FAILURE: ${res.status} - ${res.body}`);
  }

  sleep(0.5);
}
