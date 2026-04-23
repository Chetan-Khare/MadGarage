-- V30: Performance Optimization Indexes
-- These indexes target frequently filtered and sorted columns in the automotive catalog and orders.

-- 1. Vehicle Variant Lookups (Core for Fitment Search)
CREATE INDEX idx_vehicle_fitment_lookup ON vehicles (fuel_type, trim, engine_type);

-- 2. Order Date Analytics (For Last 6 Months Revenue Charts)
CREATE INDEX idx_order_date ON orders (order_date);

-- 3. Product Catalog Visibility
CREATE INDEX idx_product_flagged_category ON products (flagged, category);

-- 4. User Location Lookups (For City Selector / Tie-Up Garages)
CREATE INDEX idx_user_city_tieup ON users (city, is_tie_up, is_active);
