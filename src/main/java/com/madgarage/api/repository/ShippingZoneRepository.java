package com.madgarage.api.repository;

import com.madgarage.api.model.ShippingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShippingZoneRepository extends JpaRepository<ShippingZone, Long> {
    Optional<ShippingZone> findByStateNameIgnoreCase(String stateName);
}
