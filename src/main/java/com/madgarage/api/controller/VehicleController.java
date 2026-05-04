package com.madgarage.api.controller;

import com.madgarage.api.model.Vehicle;
import com.madgarage.api.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getCount() {
        return ResponseEntity.ok(vehicleService.getVehicleCount());
    }

    @GetMapping("/makes")
    public List<com.madgarage.api.model.Make> getMakes() {
        return vehicleService.getMakes();
    }

    @GetMapping("/models")
    public List<String> getModels(@RequestParam String make) {
        return vehicleService.getModels(make);
    }

    @GetMapping("/years")
    public List<Integer> getYears(@RequestParam String make, @RequestParam String model) {
        return vehicleService.getYears(make, model);
    }

    @GetMapping("/fuels")
    public List<String> getFuels(@RequestParam String make, @RequestParam String model, @RequestParam Integer year) {
        return vehicleService.getFuels(make, model, year);
    }

    @GetMapping("/trims")
    public List<String> getTrims(@RequestParam String make, @RequestParam String model, 
                                 @RequestParam Integer year, @RequestParam String fuel) {
        return vehicleService.getTrims(make, model, year, fuel);
    }

    @GetMapping("/engines")
    public List<String> getEngines(@RequestParam String make, @RequestParam String model, 
                                   @RequestParam Integer year, @RequestParam String fuel, @RequestParam String trim) {
        return vehicleService.getEngines(make, model, year, fuel, trim);
    }

    @GetMapping("/search")
    public List<Vehicle> searchVehicles(@RequestParam String make, @RequestParam String model, 
                                       @RequestParam Integer year, @RequestParam String fuel,
                                       @RequestParam String trim, @RequestParam String engine) {
        return vehicleService.searchVehicles(make, model, year, fuel, trim, engine);
    }

    // ─── Admin-only CRUD ──────────────────────────────────────────────────────

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<Vehicle> addVehicle(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(vehicleService.addVehicle(body));
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('WORKER')")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        if (vehicleService.deleteVehicle(id)) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}