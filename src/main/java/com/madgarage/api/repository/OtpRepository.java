package com.madgarage.api.repository;

import com.madgarage.api.model.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {
    Optional<Otp> findTopByPhoneOrderByCreatedAtDesc(String phone);
    void deleteByPhone(String phone);
}
