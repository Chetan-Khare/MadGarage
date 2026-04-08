package com.madgarage.api.services;

import com.madgarage.api.dto.Base64ProductRequest;
import com.madgarage.api.enums.FitmentCategory;
import com.madgarage.api.enums.PartCondition;
import com.madgarage.api.model.Product;
import com.madgarage.api.model.ProductImage;
import com.madgarage.api.model.User;
import com.madgarage.api.model.Vehicle;
import com.madgarage.api.repository.ProductRepository;
import com.madgarage.api.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCreationService {

    private final ProductRepository productRepository;
    private final VehicleRepository vehicleRepository;

    private static final String UPLOAD_DIR_REL = "src/main/resources/static/uploads/";
    private static final String GUIDES_DIR_REL = "src/main/resources/static/guides/";

    @Transactional
    public void addProduct(User seller,
                           List<MultipartFile> images,
                           MultipartFile guide,
                           String sku,
                           String brand,
                           String partName,
                           String category,
                           Double price,
                           String description,
                           Integer stockQuantity,
                           String color,
                           String condition,
                           String fitmentCategory,
                           List<Long> vehicleIds) {
        try {
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR_REL).toAbsolutePath().normalize();
            File uploadDirectory = uploadPath.toFile();
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }

            List<ProductImage> productImages = new ArrayList<>();
            String primaryImageUrl = null;

            if (images != null) {
                for (MultipartFile image : images) {
                    if (image == null || image.isEmpty()) continue;
                    
                    String contentType = image.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) continue;

                    String ext = contentType.split("/")[1].replaceAll("[^a-zA-Z0-9]", "");
                    String fileName = UUID.randomUUID() + "." + ext;
                    Path filePath = uploadPath.resolve(fileName).normalize();
                    
                    Files.write(filePath, image.getBytes());
                    String fileUrl = "/uploads/" + fileName;

                    if (primaryImageUrl == null) {
                        primaryImageUrl = fileUrl;
                    }

                    productImages.add(ProductImage.builder()
                            .imageUrl(fileUrl)
                            .build());
                }
            }

            if (primaryImageUrl == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one valid image is required.");
            }

            String guideUrl = null;
            if (guide != null && !guide.isEmpty()) {
                Path guidesPath = Paths.get(projectRoot, GUIDES_DIR_REL).toAbsolutePath().normalize();
                if (!guidesPath.toFile().exists()) guidesPath.toFile().mkdirs();

                String guideExt = guide.getContentType() != null ? 
                        guide.getContentType().split("/")[1].replaceAll("[^a-zA-Z0-9]", "") : "pdf";
                String guideName = UUID.randomUUID() + "." + guideExt;
                Files.write(guidesPath.resolve(guideName), guide.getBytes());
                guideUrl = "/guides/" + guideName;
            }

            List<Vehicle> compatibleVehicles = vehicleRepository.findAllById(vehicleIds != null ? vehicleIds : new ArrayList<>());

            Product newProduct = Product.builder()
                    .sku(sku)
                    .brand(brand)
                    .partName(partName)
                    .category(category)
                    .price(price)
                    .description(description)
                    .color(color)
                    .condition(PartCondition.valueOf(condition.toUpperCase()))
                    .stockQuantity(stockQuantity)
                    .imageUrl(primaryImageUrl)
                    .installationGuideUrl(guideUrl)
                    .fitmentCategory(FitmentCategory.valueOf(fitmentCategory.toUpperCase()))
                    .fittedVehicles(new HashSet<>(compatibleVehicles))
                    .images(productImages)
                    .seller(seller)
                    .build();

            for (ProductImage pi : productImages) {
                pi.setProduct(newProduct);
            }

            productRepository.save(newProduct);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Product listing failed: " + e.getMessage());
        }
    }

    @Transactional
    public void addProductBase64(User seller, Base64ProductRequest request) {
        try {
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR_REL).toAbsolutePath().normalize();
            if (!uploadPath.toFile().exists()) uploadPath.toFile().mkdirs();

            List<ProductImage> productImages = new ArrayList<>();
            String primaryImageUrl = null;

            if (request.getBase64Images() != null) {
                for (String base64 : request.getBase64Images()) {
                    if (base64 == null || base64.isEmpty() || base64.startsWith("http")) continue;
                    
                    String base64Data = base64.contains(",") ? base64.split(",")[1] : base64;
                    byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
                    
                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    Path filePath = uploadPath.resolve(fileName).normalize();
                    
                    Files.write(filePath, bytes);
                    String fileUrl = "/uploads/" + fileName;

                    if (primaryImageUrl == null) primaryImageUrl = fileUrl;
                    productImages.add(ProductImage.builder().imageUrl(fileUrl).build());
                }
            }

            if (primaryImageUrl == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one valid image is required.");
            }

            String guideUrl = null;
            if (request.getBase64Guide() != null && !request.getBase64Guide().isEmpty()) {
                Path guidesPath = Paths.get(projectRoot, GUIDES_DIR_REL).toAbsolutePath().normalize();
                if (!guidesPath.toFile().exists()) guidesPath.toFile().mkdirs();

                String base64Guard = request.getBase64Guide().contains(",") ? request.getBase64Guide().split(",")[1] : request.getBase64Guide();
                byte[] guideBytes = java.util.Base64.getDecoder().decode(base64Guard);
                String guideName = UUID.randomUUID() + "." + (request.getGuideExtension() != null ? request.getGuideExtension() : "pdf");
                Files.write(guidesPath.resolve(guideName), guideBytes);
                guideUrl = "/guides/" + guideName;
            }

            List<Vehicle> compatibleVehicles = vehicleRepository.findAllById(request.getVehicleIds() != null ? request.getVehicleIds() : new ArrayList<>());

            Product newProduct = Product.builder()
                    .sku(request.getSku())
                    .brand(request.getBrand())
                    .partName(request.getPartName())
                    .category(request.getCategory())
                    .price(request.getPrice())
                    .description(request.getDescription())
                    .color(request.getColor())
                    .condition(PartCondition.valueOf(request.getCondition().toUpperCase()))
                    .stockQuantity(request.getStockQuantity())
                    .imageUrl(primaryImageUrl)
                    .installationGuideUrl(guideUrl)
                    .fitmentCategory(FitmentCategory.valueOf(request.getFitmentCategory().toUpperCase()))
                    .fittedVehicles(new HashSet<>(compatibleVehicles))
                    .images(productImages)
                    .seller(seller)
                    .build();

            for (ProductImage pi : productImages) {
                pi.setProduct(newProduct);
            }

            productRepository.save(newProduct);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Base64 Product listing failed: " + e.getMessage());
        }
    }

    @Transactional
    public void updateProductBase64(User seller, Long productId, Base64ProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));

        if (product.getSeller() == null || !product.getSeller().getId().equals(seller.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to edit this product.");
        }

        try {
            String projectRoot = System.getProperty("user.dir");
            Path uploadPath = Paths.get(projectRoot, UPLOAD_DIR_REL).toAbsolutePath().normalize();

            if (request.getBase64Images() != null && !request.getBase64Images().isEmpty()) {
                if (!uploadPath.toFile().exists()) uploadPath.toFile().mkdirs();
                
                List<ProductImage> newImages = new ArrayList<>();
                String primaryImageUrl = null;

                for (String base64 : request.getBase64Images()) {
                    if (base64 == null || base64.isEmpty() || base64.startsWith("http")) continue;
                    
                    String base64Data = base64.contains(",") ? base64.split(",")[1] : base64;
                    byte[] bytes = java.util.Base64.getDecoder().decode(base64Data);
                    
                    String fileName = UUID.randomUUID().toString() + ".jpg";
                    Path filePath = uploadPath.resolve(fileName).normalize();
                    
                    Files.write(filePath, bytes);
                    String fileUrl = "/uploads/" + fileName;

                    if (primaryImageUrl == null) primaryImageUrl = fileUrl;
                    newImages.add(ProductImage.builder().imageUrl(fileUrl).product(product).build());
                }

                if (primaryImageUrl != null) {
                    product.getImages().clear();
                    product.getImages().addAll(newImages);
                    product.setImageUrl(primaryImageUrl);
                }
            }

            if (request.getBase64Guide() != null && !request.getBase64Guide().isEmpty()) {
                Path guidesPath = Paths.get(projectRoot, GUIDES_DIR_REL).toAbsolutePath().normalize();
                if (!guidesPath.toFile().exists()) guidesPath.toFile().mkdirs();

                String base64Guard = request.getBase64Guide().contains(",") ? request.getBase64Guide().split(",")[1] : request.getBase64Guide();
                byte[] guideBytes = java.util.Base64.getDecoder().decode(base64Guard);
                String guideName = UUID.randomUUID() + "." + (request.getGuideExtension() != null ? request.getGuideExtension() : "pdf");
                Files.write(guidesPath.resolve(guideName), guideBytes);
                product.setInstallationGuideUrl("/guides/" + guideName);
            }

            List<Vehicle> compatibleVehicles = vehicleRepository.findAllById(request.getVehicleIds() != null ? request.getVehicleIds() : new ArrayList<>());

            product.setSku(request.getSku());
            product.setBrand(request.getBrand());
            product.setPartName(request.getPartName());
            product.setCategory(request.getCategory());
            product.setPrice(request.getPrice());
            product.setDescription(request.getDescription());
            product.setColor(request.getColor());
            
            if (request.getCondition() != null) {
                product.setCondition(PartCondition.valueOf(request.getCondition().toUpperCase()));
            }
            if (request.getFitmentCategory() != null) {
                product.setFitmentCategory(FitmentCategory.valueOf(request.getFitmentCategory().toUpperCase()));
            }
            product.setStockQuantity(request.getStockQuantity());
            product.setFittedVehicles(new HashSet<>(compatibleVehicles));

            productRepository.save(product);

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Product update failed: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteSellerProduct(User seller, Long productId) {
        return productRepository.findById(productId)
                .filter(p -> p.getSeller() != null && p.getSeller().getId().equals(seller.getId()))
                .map(p -> {
                    productRepository.delete(p);
                    return true;
                })
                .orElse(false);
    }

    @Transactional
    public boolean addSellerResponse(User seller, Long productId, String response) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found."));

        if (product.getSeller() == null || !product.getSeller().getId().equals(seller.getId())) {
            return false;
        }

        product.setSellerResponse(response);
        productRepository.save(product);
        return true;
    }
}
