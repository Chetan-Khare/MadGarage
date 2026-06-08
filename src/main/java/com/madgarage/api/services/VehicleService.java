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
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private final SimpMessagingTemplate messagingTemplate;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAllByOrderByMakeAsc();
    }

    public long getVehicleCount() {
        return vehicleRepository.count();
    }

    @Cacheable("vehicle_makes")
    public List<Make> getMakes() {
        return makeRepository.findAllByOrderByNameAsc();
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
    public List<Vehicle> addVehicle(Map<String, Object> body) {
        String makeName = body.get("make") != null ? body.get("make").toString().trim() : "";
        String modelName = body.get("model") != null ? body.get("model").toString().trim() : "";
        
        // 1. Sync Make
        Make makeObj = makeRepository.findByName(makeName)
                .orElseGet(() -> makeRepository.save(Make.builder().name(makeName).build()));

        // 2. Sync CarModel
        CarModel modelObj = carModelRepository.findByNameAndMake(modelName, makeObj)
                .orElseGet(() -> carModelRepository.save(CarModel.builder()
                        .name(modelName)
                        .make(makeObj)
                        .build()));

        String fuelType = body.getOrDefault("fuelType", "Petrol").toString();
        String engineType = body.getOrDefault("engineType", "").toString();
        
        String trimRaw = body.getOrDefault("trim", "").toString();
        String[] trims = trimRaw.isEmpty() ? new String[]{""} : trimRaw.split(",");
        
        String yearRaw = body.get("year") != null ? body.get("year").toString() : "2024";
        String[] years = yearRaw.split(",");

        Long editId = null;
        if (body.containsKey("id") && body.get("id") != null) {
            editId = Long.parseLong(body.get("id").toString());
        }

        List<Vehicle> savedVehicles = new java.util.ArrayList<>();
        boolean isFirst = true;

        for (String yearStr : years) {
            int year = Integer.parseInt(yearStr.trim());
            for (String trimStr : trims) {
                String trim = trimStr.trim();
                
                Vehicle vehicle;
                if (isFirst && editId != null) {
                    vehicle = vehicleRepository.findById(editId).orElse(new Vehicle());
                    isFirst = false;
                } else {
                    vehicle = new Vehicle();
                }
                
                vehicle.setMake(makeName);
                vehicle.setModel(modelName);
                vehicle.setCarModel(modelObj);
                vehicle.setTrim(trim);
                vehicle.setFuelType(fuelType);
                vehicle.setEngineType(engineType);
                vehicle.setYear(year);

                List<Vehicle> existingList = vehicleRepository.findByMakeAndModelAndYearAndFuelTypeAndTrimAndEngineType(
                        makeName, modelName, year, fuelType, trim, engineType);
                
                if (existingList != null && !existingList.isEmpty()) {
                    Vehicle existing = existingList.get(0);
                    if (vehicle.getId() == null) {
                        // Creating new, but it already exists. Skip insert.
                        savedVehicles.add(existing);
                        continue;
                    } else if (!existing.getId().equals(vehicle.getId())) {
                        // COLLISION DURING EDIT!
                        // The user changed a vehicle to match one that ALREADY exists.
                        // We safely delete the old one (moving fitments) and keep the existing one.
                        vehicleRepository.deleteFitmentsByVehicleId(vehicle.getId());
                        vehicleRepository.deleteById(vehicle.getId());
                        
                        // Tell UI to remove the old typo row
                        messagingTemplate.convertAndSend("/topic/vehicles/updates", (Object) Map.of(
                            "type", "DELETE",
                            "payload", vehicle.getId()
                        ));
                        
                        savedVehicles.add(existing);
                        continue;
                    }
                }

                Vehicle saved = vehicleRepository.save(vehicle);
                savedVehicles.add(saved);
                
                messagingTemplate.convertAndSend("/topic/vehicles/updates", (Object) Map.of(
                    "type", "ADD",
                    "payload", saved
                ));
            }
        }
        
        return savedVehicles;
    }

    @Transactional
    @CacheEvict(value = {"vehicle_makes", "vehicle_models", "vehicle_years", "vehicle_fuels", "vehicle_trims", "vehicle_engines"}, allEntries = true)
    public boolean deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            return false;
        }
        
        // Remove fitment references from products before deleting the vehicle to prevent Foreign Key constraint violation
        vehicleRepository.deleteFitmentsByVehicleId(id);
        
        vehicleRepository.deleteById(id);
        
        messagingTemplate.convertAndSend("/topic/vehicles/updates", (Object) Map.of(
            "type", "DELETE",
            "payload", id
        ));
        
        return true;
    }

    private String capitalizeWords(String str) {
        if (str == null || str.trim().isEmpty()) return str;
        String[] words = str.toLowerCase().trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Transactional
    public String cleanupDuplicateMakesAndModels() {
        // 1. Normalize Vehicle string columns
        List<Vehicle> allVehicles = vehicleRepository.findAll();
        int vUpdated = 0;
        for (Vehicle v : allVehicles) {
            String normMake = capitalizeWords(v.getMake());
            String normModel = capitalizeWords(v.getModel());
            boolean changed = false;
            if (!normMake.equals(v.getMake())) {
                v.setMake(normMake);
                changed = true;
            }
            if (!normModel.equals(v.getModel())) {
                v.setModel(normModel);
                changed = true;
            }
            if (changed) {
                // Check if the newly normalized combination collides with an already existing one
                List<Vehicle> existingList = vehicleRepository.findByMakeAndModelAndYearAndFuelTypeAndTrimAndEngineType(
                        v.getMake(), v.getModel(), v.getYear(), v.getFuelType(), v.getTrim(), v.getEngineType()
                );
                
                if (existingList != null && !existingList.isEmpty()) {
                    Vehicle existing = existingList.get(0);
                    if (!existing.getId().equals(v.getId())) {
                        // Collision detected! The normalized version already exists.
                        // We safely delete this duplicate (which had the trailing space/typo)
                        vehicleRepository.deleteFitmentsByVehicleId(v.getId());
                        vehicleRepository.delete(v);
                        continue;
                    }
                }
                
                vehicleRepository.save(v);
                vUpdated++;
            }
        }

        // 2. Normalize and merge Make entities
        List<Make> allMakes = makeRepository.findAll();
        Map<String, Make> standardizedMakes = new java.util.HashMap<>();
        int mDeleted = 0;
        
        for (Make m : allMakes) {
            String capName = capitalizeWords(m.getName());
            if (!standardizedMakes.containsKey(capName)) {
                // Keep this one
                if (!capName.equals(m.getName())) {
                    m.setName(capName);
                    makeRepository.save(m);
                }
                standardizedMakes.put(capName, m);
            } else {
                // Duplicate! Repoint models to the standard one and delete this duplicate.
                Make stdMake = standardizedMakes.get(capName);
                for (CarModel cm : new java.util.ArrayList<>(m.getModels())) {
                    cm.setMake(stdMake);
                    carModelRepository.save(cm);
                }
                makeRepository.delete(m);
                mDeleted++;
            }
        }

        // 3. Normalize and merge CarModel entities
        List<CarModel> allModels = carModelRepository.findAll();
        Map<String, CarModel> standardizedModels = new java.util.HashMap<>();
        int cmDeleted = 0;
        
        for (CarModel cm : allModels) {
            // Key is combination of make ID and model name to ensure uniqueness
            String capName = capitalizeWords(cm.getName());
            String key = cm.getMake().getId() + "-" + capName;
            
            if (!standardizedModels.containsKey(key)) {
                if (!capName.equals(cm.getName())) {
                    cm.setName(capName);
                    carModelRepository.save(cm);
                }
                standardizedModels.put(key, cm);
            } else {
                // Duplicate CarModel under the same make
                CarModel stdModel = standardizedModels.get(key);
                // Repoint any vehicles using this model to the standard one
                List<Vehicle> duplicateVehicles = new java.util.ArrayList<>(cm.getVehicles());
                for (Vehicle v : duplicateVehicles) {
                    v.setCarModel(stdModel);
                    vehicleRepository.save(v);
                }
                carModelRepository.delete(cm);
                cmDeleted++;
            }
        }

        return String.format("Cleanup complete: Updated %d vehicle text strings. Merged %d duplicate Makes. Merged %d duplicate CarModels.", vUpdated, mDeleted, cmDeleted);
    }
}
