import http from 'k6/http';
import { check, sleep } from 'k6';

// -------------------------------------------------------------------------
// Base64 Upload Load Test: Seller Listing Flow
// -------------------------------------------------------------------------

export const options = {
  stages: [
    { duration: '20s', target: 5 },  // Start with 5 sellers
    { duration: '40s', target: 10 }, // Increase to 10 sellers
    { duration: '20s', target: 0 },  // Cool down
  ],
  thresholds: {
    http_req_duration: ['p(95)<1500'], // Image processing is heavy, allowing 1.5s
    http_req_failed: ['rate<0.05'],     // Allowing up to 5% failure for stress
  },
};

const BASE_URL = 'http://localhost:8080/api';

// A tiny 1x1 transparent PNG base64 string for testing
const DUMMY_IMAGE = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8/5+hHgAHggJ/PchI7wAAAABJRU5ErkJggg==";

export default function () {
  // 1. LOGIN as Seller (Modify credentials if needed)
  const loginPayload = JSON.stringify({
    email: 'seller@madgarage.com',
    password: 'password123'
  });

  const params = {
    headers: { 'Content-Type': 'application/json' },
  };

  // Note: This login might fail if the user doesn't exist yet. 
  // In a real test, we would have a pre-registered seller.
  let loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, params);
  
  if (loginRes.status !== 200) {
    // If login fails, try to register the seller once
    http.post(`${BASE_URL}/auth/register`, JSON.stringify({
      email: 'stress_test_seller@madgarage.com',
      password: 'password123',
      firstName: 'Stress',
      lastName: 'Seller',
      phone: '1234567890',
      role: 'SELLER'
    }), params);
    
    loginRes = http.post(`${BASE_URL}/auth/login`, loginPayload, params);
  }

  const token = loginRes.json().token;
  const authParams = {
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
  };

  // 2. UPLOAD Product via Base64
  const productPayload = JSON.stringify({
    sku: `STRESS-${Math.floor(Math.random() * 1000000)}`,
    brand: 'Performance-Test',
    partName: 'Turbo Charger',
    category: 'Engine',
    price: 45000.0,
    description: 'Load test generated product',
    stockQuantity: 10,
    condition: 'NEW',
    fitmentCategory: 'ENGINE',
    base64Images: [DUMMY_IMAGE]
  });

  let res = http.post(`${BASE_URL}/seller/inventory/base64`, productPayload, authParams);

  check(res, {
    'upload status is 200': (r) => r.status === 200,
    'upload success message': (r) => r.body.includes('successfully'),
  });

  sleep(2); // Wait between uploads
}
