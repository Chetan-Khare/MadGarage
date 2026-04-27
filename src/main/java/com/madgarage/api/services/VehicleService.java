package com.madgarage.api.services;

import com.madgarage.api.model.CarModel;
import com.madgarage.api.model.Make;
import com.madgarage.api.model.Vehicle;
import com.madgarage.api.repository.CarModelRepository;
import com.madgarage.api.repository.MakeRepository;
import com.madgarage.api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final MakeRepository makeRepository;
    private final CarModelRepository carModelRepository;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllByOrderByMakeAsc();
    }

    public long getVehicleCount() {
        return vehicleRepository.count();
    }

    @Cacheable("vehicle_makes")
    public List<String> getMakes() {
        return vehicleRepository.findDistinctMakes();
    }

    @Cacheable(value = "vehicle_models", key = "#make")
    public List<String> getModels(String make) {
        return vehicleRepository.findDistinctModelsByMake(make);
    }

    @Cacheable(value = "vehicle_years", key = "#make + '-' + #model")
    public List<Integer> getYears(String make, String model) {
        return vehicleRepository.findDistinctYearsByMakeAndModel(make, model);
    }

    @Cacheable(value = "vehicle_fuels", key = "#make + '-' + #model + '-' + #year")
    public List<String> getFuels(String make, String model, Integer year) {
        return vehicleRepository.findDistinctFuelsByMakeAndModelAndYear(make, model, year);
    }

    @Cacheable(value = "vehicle_trims", key = "#make + '-' + #model + '-' + #year + '-' + #fuel")
    public List<String> getTrims(String make, String model, Integer year, String fuel) {
        return vehicleRepository.findDistinctTrimsByMakeAndModelAndYearAndFuel(make, model, year, fuel);
    }

    @Cacheable(value = "vehicle_engines", key = "#make + '-' + #model + '-' + #year + '-' + #fuel + '-' + #trim")
    public List<String> getEngines(String make, String model, Integer year, String fuel, String trim) {
        return vehicleRepository.findDistinctEngineTypesByVariant(make, model, year, fuel, trim);
    }

    public List<Vehicle> searchVehicles(String make, String model, Integer year, String fuel, String trim, String engine) {
        return vehicleRepository.findByMakeAndModelAndYearAndFuelTypeAndTrimAndEngineType(make, model, year, fuel, trim, engine);
    }

    @Transactional
    @CacheEvict(value = {"vehicle_makes", "vehicle_models", "vehicle_years", "vehicle_fuels", "vehicle_trims", "vehicle_engines"}, allEntries = true)
    public Vehicle addVehicle(Map<String, Object> body) {
        String makeName = (String) body.get("make");
        String modelName = (String) body.get("model");
        
        // 1. Sync Make
        Make makeObj = makeRepository.findByName(makeName)
                .orElseGet(() -> makeRepository.save(Make.builder().name(makeName).build()));

        // 2. Sync CarModel
        CarModel modelObj = carModelRepository.findByNameAndMake(modelName, makeObj)
                .orElseGet(() -> carModelRepository.save(CarModel.builder()
                        .name(modelName)
                        .make(makeObj)
                        .build()));

        // 3. Create Vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(makeName);
        vehicle.setModel(modelName);
        vehicle.setCarModel(modelObj);
        vehicle.setTrim((String) body.getOrDefault("trim", ""));
        vehicle.setFuelType((String) body.getOrDefault("fuelType", "Petrol"));
        vehicle.setEngineType((String) body.getOrDefault("engineType", ""));

        Object yearObj = body.get("year");
        if (yearObj instanceof Integer) {
            vehicle.setYear((Integer) yearObj);
        } else if (yearObj != null) {
            vehicle.setYear(Integer.parseInt(yearObj.toString()));
        }

        return vehicleRepository.save(vehicle);
    }

    @Transactional
    @CacheEvict(value = {"vehicle_makes", "vehicle_models", "vehicle_years", "vehicle_fuels", "vehicle_trims", "vehicle_engines"}, allEntries = true)
    public boolean deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            return false;
        }
        vehicleRepository.deleteById(id);
        return true;
    }
}
