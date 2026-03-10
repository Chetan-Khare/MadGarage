package com.madgarage.api.config;

import com.madgarage.api.enums.Role;
import com.madgarage.api.model.*;
import com.madgarage.api.repository.*;
import com.madgarage.api.services.VehicleSyncService; // <-- Import the new service
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DatabaseSeeder {

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            ProductRepository productRepository,
            PasswordEncoder passwordEncoder,
            MakeRepository makeRepository,
            CarModelRepository carModelRepository,
            VehicleRepository vehicleRepository,
            VehicleSyncService vehicleSyncService) { // <-- Inject it here

        return args -> {
            // 1. Check if the database is empty so we don't duplicate data on every restart
            if (makeRepository.count() == 0) {

                // 2. Create an Admin User to test login
                User admin = User.builder()
                        .firstName("Super")
                        .lastName("Admin")
                        .email("admin@madgarage.com")
                        .password(passwordEncoder.encode("password123"))
                        .role(Role.ADMIN)
                        .isActive(true)
                        .build();
                userRepository.save(admin);

                // 3. Create our manual test hierarchy (Hyundai -> Creta -> 2024 Model)
                Make hyundai = Make.builder().name("Hyundai").build();
                makeRepository.save(hyundai);

                CarModel creta = CarModel.builder().name("Creta").make(hyundai).build();
                carModelRepository.save(creta);

                Vehicle creta2024 = Vehicle.builder()
                        .make("Hyundai")
                        .model("Creta")
                        .year(2024)
                        .engineType("1.5L Petrol")
                        .fuelType("Petrol")
                        .carModel(creta)
                        .generation("Facelift") // Using our new field!
                        .trim("SX(O)")          // Using our new field!
                        .build();
                vehicleRepository.save(creta2024);

                // 4. Add Inventory and link it to the Vehicle
                Product brakePads = Product.builder()
                        .sku("BP-HYU-CRT-001")
                        .brand("Bosch")
                        .partName("Front Brake Pads")
                        .category("Brakes")
                        .price(1250.00)
                        .description("Premium ceramic front brake pads for Hyundai Creta.")
                        .stockQuantity(15)
                        .build();
                brakePads.getFittedVehicles().add(creta2024);
                productRepository.save(brakePads);

                System.out.println("✅ Database seeded with Mad Garage test data!");

                // 5. TEST THE API FETCHER
                System.out.println("🔄 Fetching live data from NHTSA API...");
                vehicleSyncService.fetchAndSaveModelsForMake("Toyota"); // Let's fetch Toyota for the test!
            }
        };
    }
}