package com.madgarage.api.repository;

import com.madgarage.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p JOIN p.fittedVehicles v " +
            "WHERE v.carModel.make.name = :makeName " +
            "AND v.carModel.name = :modelName " +
            "AND v.year = :year " +
            "AND LOWER(v.trim) = LOWER(:trim) " +
            "AND p.category = :partCategory") // Simplified to match Product.java
    List<Product> findGuaranteedFitParts(
            @Param("makeName") String makeName,
            @Param("modelName") String modelName,
            @Param("year") Integer year,
            @Param("trim") String trim,
            @Param("partCategory") String partCategory);
}