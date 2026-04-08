package com.madgarage.api.repository;

import com.madgarage.api.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findAllByOrderByMakeAsc();

    @Query("SELECT DISTINCT v.make FROM Vehicle v ORDER BY v.make ASC")
    List<String> findDistinctMakes();

    @Query("SELECT DISTINCT v.model FROM Vehicle v WHERE v.make = :make ORDER BY v.model ASC")
    List<String> findDistinctModelsByMake(@Param("make") String make);

    @Query("SELECT DISTINCT v.year FROM Vehicle v WHERE v.make = :make AND v.model = :model ORDER BY v.year DESC")
    List<Integer> findDistinctYearsByMakeAndModel(@Param("make") String make, @Param("model") String model);

    @Query("SELECT DISTINCT v.fuelType FROM Vehicle v WHERE v.make = :make AND v.model = :model AND v.year = :year ORDER BY v.fuelType ASC")
    List<String> findDistinctFuelsByMakeAndModelAndYear(@Param("make") String make, @Param("model") String model, @Param("year") Integer year);

    @Query("SELECT DISTINCT v.trim FROM Vehicle v WHERE v.make = :make AND v.model = :model AND v.year = :year AND v.fuelType = :fuel ORDER BY v.trim ASC")
    List<String> findDistinctTrimsByMakeAndModelAndYearAndFuel(@Param("make") String make, @Param("model") String model, @Param("year") Integer year, @Param("fuel") String fuel);

    @Query("SELECT DISTINCT v.engineType FROM Vehicle v WHERE v.make = :make AND v.model = :model AND v.year = :year AND v.fuelType = :fuel AND v.trim = :trim ORDER BY v.engineType ASC")
    List<String> findDistinctEngineTypesByVariant(@Param("make") String make, @Param("model") String model, @Param("year") Integer year, @Param("fuel") String fuel, @Param("trim") String trim);

    List<Vehicle> findByMakeAndModelAndYearAndFuelTypeAndTrimAndEngineType(String make, String model, Integer year, String fuel, String trim, String engine);
}