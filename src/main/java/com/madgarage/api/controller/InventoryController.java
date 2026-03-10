package com.madgarage.api.controller;

import com.madgarage.api.model.Product;
import com.madgarage.api.model.Vehicle;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.repository.VehicleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/seller/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final ProductRepository productRepository;
    private final VehicleRepository vehicleRepository;

    // A folder on your computer to save the uploaded product photos
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public InventoryController(ProductRepository productRepository, VehicleRepository vehicleRepository) {
        this.productRepository = productRepository;
        this.vehicleRepository = vehicleRepository;
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<?> addProductWithImageAndFitment(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "guide", required = false)MultipartFile guide,
            @RequestParam("sku") String sku,
            @RequestParam("brand") String brand,
            @RequestParam("partName") String partName,
            @RequestParam("category") String category,
            @RequestParam("price") Double price,
            @RequestParam("description") String description,
            @RequestParam("stockQuantity") Integer stockQuantity,
            @RequestParam(value = "color", required = false) String color,
            @RequestParam("vehicleIds") List<Long> vehicleIds) {

        try {
            // 1. Save the Image File
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) directory.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, image.getBytes());

            // 2. Fetch Compatible Vehicles
            List<Vehicle> compatibleVehicles = vehicleRepository.findAllById(vehicleIds);
            String guideUrl = null;
            if (guide != null && !guide.isEmpty()) {
                String guideName = System.currentTimeMillis() + "_" + guide.getOriginalFilename();
                Path guidePath = Paths.get("src/main/resources/static/guides/" + guideName);

                // Create guides folder if it doesn't exist
                File guideDir = new File("src/main/resources/static/guides/");
                if (!guideDir.exists()) guideDir.mkdirs();

                Files.write(guidePath, guide.getBytes());
                guideUrl = "/guides/" + guideName;
            }

            // 3. Create and Save the Product using the Lombok Builder
            Product newProduct = Product.builder()
                    .sku(sku)
                    .brand(brand)
                    .partName(partName)
                    .category(category)
                    .price(price)
                    .description(description)
                    .color(color)
                    .stockQuantity(stockQuantity)
                    .imageUrl("/uploads/" + fileName)
                    .installationGuideUrl(guideUrl)
                    .fittedVehicles(new HashSet<>(compatibleVehicles))
                    .build();

            productRepository.save(newProduct);

            return ResponseEntity.ok("Product and Guide saved successfully!");

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("File upload failed.");
        }
    }
}