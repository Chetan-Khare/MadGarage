package com.madgarage.api.repository;

import com.madgarage.api.model.PartRequest;
import com.madgarage.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
    List<PartRequest> findByUserOrderByCreatedAtDesc(User user);
    List<PartRequest> findAllByOrderByCreatedAtDesc();
}
