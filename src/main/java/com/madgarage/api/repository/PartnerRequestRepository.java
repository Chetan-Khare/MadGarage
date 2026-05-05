package com.madgarage.api.repository;

import com.madgarage.api.model.PartnerRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PartnerRequestRepository extends JpaRepository<PartnerRequest, Long> {
    List<PartnerRequest> findAllByOrderByCreatedAtDesc();
    List<PartnerRequest> findByStatusOrderByCreatedAtDesc(PartnerRequest.RequestStatus status);
}
