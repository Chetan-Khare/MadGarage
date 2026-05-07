package com.madgarage.api.repository;

import com.madgarage.api.enums.ReturnStatus;
import com.madgarage.api.model.ReturnRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReturnRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUserId(Long userId);
    Optional<ReturnRequest> findByOrderId(Long orderId);
    List<ReturnRequest> findByStatus(ReturnStatus status);
}
