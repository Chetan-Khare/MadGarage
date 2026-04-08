package com.madgarage.api.repository;
import com.madgarage.api.model.CarModel;
import com.madgarage.api.model.Make;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long>{
    Optional<CarModel> findByName(String name);
    Optional<CarModel> findByNameAndMake(String name, Make make);
}
