package com.madgarage.api.repository;

import com.madgarage.api.model.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MakeRepository extends JpaRepository<Make, Long> {
    Optional<Make> findByName(String name);
    List<Make> findAllByOrderByNameAsc();
}