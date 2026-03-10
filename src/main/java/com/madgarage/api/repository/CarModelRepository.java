package com.madgarage.api.repository;
import com.madgarage.api.model.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface CarModelRepository extends JpaRepository<CarModel, Long>{
}
