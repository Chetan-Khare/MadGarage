package com.madgarage.api.repository;

import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.model.Product;
import com.madgarage.api.model.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

        @EntityGraph(attributePaths = {"images"})
        @Query("SELECT p FROM Product p JOIN p.fittedVehicles v " +
                        "WHERE v.carModel.make.name = :makeName " +
                        "AND v.carModel.name = :modelName " +
                        "AND v.year = :year " +
                        "AND LOWER(v.fuelType) = LOWER(:fuelType) " +
                        "AND LOWER(v.trim) = LOWER(:trim) " +
                        "AND LOWER(v.engineType) = LOWER(:engineType) " +
                        "AND p.category = :partCategory")
        List<Product> findGuaranteedFitParts(
                        @Param("makeName") String makeName,
                        @Param("modelName") String modelName,
                        @Param("year") Integer year,
                        @Param("fuelType") String fuelType,
                        @Param("trim") String trim,
                        @Param("engineType") String engineType,
                        @Param("partCategory") String partCategory);

        @EntityGraph(attributePaths = {"images"})
        List<Product> findByFittedVehiclesId(Long vehicleId);

        @EntityGraph(attributePaths = {"images"})
        List<Product> findByCategoryAndFittedVehiclesId(String category, Long vehicleId);

        @EntityGraph(attributePaths = {"images"})
        List<Product> findByFitmentCategory(FitmentCategory fitmentCategory);

        @EntityGraph(attributePaths = {"images"})
        List<Product> findAll();

        long countBySeller(User seller);

        @EntityGraph(attributePaths = {"images"})
        List<Product> findBySeller(User seller);
}