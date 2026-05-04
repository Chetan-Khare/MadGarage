package com.madgarage.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * ExpoNotificationService fires push notifications to Expo's Push API.
 * Docs: https://docs.expo.dev/push-notifications/sending-notifications/
 * This is a fire-and-forget service; failures are logged but do not affect the primary status update.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ExpoNotificationService {

    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private final ObjectMapper objectMapper;

    // Status-to-human-readable mapping
    private static final Map<String, String[]> STATUS_MESSAGES = Map.of(
        "QUOTED",       new String[]{"Quote Ready! 💰", "Great news! We found your part. Tap to see the quote."},
        "FULFILLED",    new String[]{"Part Request Fulfilled! 🎉", "Your requested part has been sourced and is ready."},
        "UNAVAILABLE",  new String[]{"Part Unavailable 😔", "Unfortunately we couldn't source this part. Tap for details."},
        "PENDING",      new String[]{"Request Received ✅", "Your part request is under review by our team."}
    );

    /**
     * Sends a push notification to the given Expo push token when a part request status changes.
     *
     * @param expoPushToken The user's Expo push token (e.g. "ExponentPushToken[xxx]")
     * @param partName      The name of the requested part
     * @param newStatus     The new status string (e.g. "QUOTED")
     */
    public void sendStatusUpdateNotification(String expoPushToken, String partName, String newStatus) {
        if (expoPushToken == null || expoPushToken.isBlank()) {
            return;
        }

        String[] messages = STATUS_MESSAGES.getOrDefault(newStatus.toUpperCase(),
                new String[]{"Part Request Update", "Your part request status has been updated."});

        String title = messages[0];
        String body = partName != null ? partName + " — " + messages[1] : messages[1];

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/json");

            Map<String, Object> payload = new HashMap<>();
            payload.put("to", expoPushToken);
            payload.put("title", title);
            payload.put("body", body);
            payload.put("sound", "default");
            payload.put("data", Map.of("type", "PART_REQUEST_STATUS", "status", newStatus));

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(payload), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(EXPO_PUSH_URL, entity, String.class);

            log.info("[Expo Push] Sent notification to {} | Status: {} | HTTP: {}",
                    expoPushToken, newStatus, response.getStatusCode());

        } catch (Exception e) {
            log.error("[Expo Push] Failed to send push notification: {}", e.getMessage());
            // Intentionally NOT re-throwing — push failure should not fail the status update request
        }
    }
}
