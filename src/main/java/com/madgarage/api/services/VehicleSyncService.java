package com.madgarage.api.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- New import
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VehicleSyncService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // <-- Add ObjectMapper

    public VehicleSyncService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper(); // <-- Initialize it
    }

    public void fetchAndSaveModelsForMake(String makeName) {
        String apiUrl = "https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformake/" + makeName + "?format=json";

        try {
            // 1. Fetch the raw JSON as a simple String (Bulletproof)
            String rawJson = restTemplate.getForObject(apiUrl, String.class);

            if (rawJson != null) {
                // 2. Parse the String into a JsonNode safely
                JsonNode response = objectMapper.readTree(rawJson);

                if (response.has("Results")) {
                    JsonNode results = response.get("Results");

                    // 3. Loop through the data
                    for (JsonNode node : results) {
                        String fetchedMake = node.get("Make_Name").asText();
                        String fetchedModel = node.get("Model_Name").asText();

                        System.out.println("Found: " + fetchedMake + " " + fetchedModel);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to fetch vehicle data: " + e.getMessage());
        }
    }
}