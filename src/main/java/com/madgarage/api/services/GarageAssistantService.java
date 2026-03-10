package com.madgarage.api.services;

import com.madgarage.api.dto.AiResponseDto;
import com.madgarage.api.model.Product;
import com.madgarage.api.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

@Service
public class GarageAssistantService {

    private final ChatClient chatClient;
    private final ProductRepository productRepository;

    public record AssistantResult(String message, List<Product> products, boolean needsMoreInfo) {}

    // ── Common greetings / small-talk keywords – handled locally, NO AI call ──
    private static final Set<String> GREETINGS = Set.of(
        "hi", "hello", "hey", "yo", "sup", "hiya", "howdy",
        "good morning", "good evening", "good afternoon",
        "help", "what can you do", "who are you"
    );

    public GarageAssistantService(ChatClient.Builder chatClientBuilder, ProductRepository productRepository) {
        this.chatClient = chatClientBuilder
                .defaultOptions(GoogleGenAiChatOptions.builder()
                        .model("gemini-1.5-flash")
                        .build())
                .build();
        this.productRepository = productRepository;
    }

    public AssistantResult analyzeCarAndFindParts(String userText, byte[] uploadedImage) {

        // ── Guard: treat null/empty message as a greeting ──────────────────────
        String text = (userText == null || userText.isBlank()) ? "hi" : userText.trim();

        // ── Fast-path: conversational greetings → no AI/DB call needed ─────────
        if (isGreeting(text)) {
            return new AssistantResult(
                "Hey there! 👋 I'm your Virtual Mechanic at Mad Garage!\n\n" +
                "I can help you find the right parts for your vehicle. Just tell me:\n" +
                "• Your vehicle's **Year, Make & Model** (e.g. \"2019 Hyundai Creta\")\n" +
                "• What part you're looking for (e.g. brake pads, air filter)\n\n" +
                "Or upload a photo of the part or damage and I'll take a look! 🔧",
                new ArrayList<>(),
                false
            );
        }

        // ── System Prompt ───────────────────────────────────────────────────────
        String systemPrompt = """
        You are the Mad Garage Virtual Mechanic — a friendly, knowledgeable assistant \
        who helps customers find the right car parts.

        PERSONALITY:
        - Warm, helpful, and conversational.
        - If the user sends a casual greeting or asks who you are, introduce yourself \
          as\"Your Mad Garage Virtual Mechanic\" and invite them to share their vehicle details.
        - NEVER respond with an error or say you can't identify a vehicle just because \
          the user is chatting casually.

        GOAL — Extract vehicle details when present:
        Extract (Make, Model, Year, Trim) from the user's message, if provided.

        RULES:
        1. Every car has specific trim levels (e.g., Hyundai Creta: SX, SX(O); \
           Mahindra Scorpio: AX, LX).
        2. If the user gives a model but NO trim, set needsMoreInfo=true and ask \
           which trim specifically: "I see you have a [Model]. Is that the [Trim A] or [Trim B]?"
        3. If the message has NO vehicle info at all (e.g., just a greeting or a \
           general question), set needsMoreInfo=true and set message to a friendly \
           introductory response asking for their vehicle details.
        4. Do NOT guess. Do NOT say "I can't identify". Be friendly always.
        5. Once you have all details including trim, set needsMoreInfo=false.
        """;

        try {
            // ── Build the AI prompt (optionally with image) ──────────────────────
            var promptSpec = chatClient.prompt()
                    .system(systemPrompt)
                    .user(u -> {
                        u.text(text);
                        if (uploadedImage != null && uploadedImage.length > 0) {
                            u.media(MimeTypeUtils.IMAGE_JPEG,
                                    new ByteArrayResource(uploadedImage));
                        }
                    });

            AiResponseDto aiData = promptSpec.call().entity(AiResponseDto.class);

            // ── If AI still needs more info (no vehicle/trim), return its question ──
            if (aiData == null) {
                return new AssistantResult(
                    "Hey! 👋 I'm your Virtual Mechanic. Tell me your vehicle's Year, Make & Model and I'll find the right parts!",
                    new ArrayList<>(), true
                );
            }

            if (aiData.needsMoreInfo()) {
                String reply = (aiData.message() != null && !aiData.message().isBlank())
                    ? aiData.message()
                    : "Could you share a bit more about your vehicle? I'll need the Year, Make, Model, and trim to find the perfect parts for you! 🔧";
                return new AssistantResult(reply, new ArrayList<>(), true);
            }

            // ── All info collected — query the database ──────────────────────────
            List<Product> products = productRepository.findGuaranteedFitParts(
                    aiData.make(),
                    aiData.model(),
                    aiData.year(),
                    aiData.trim(),
                    aiData.category()
            );

            String successMsg = products.isEmpty()
                ? "I looked up parts for your " + aiData.year() + " " + aiData.make() + " " + aiData.model()
                  + " (" + aiData.trim() + ") but didn't find an exact match yet. Our inventory is updated regularly — check back soon! 🔧"
                : "Great news! 🎉 I found " + products.size() + " part(s) for your "
                  + aiData.year() + " " + aiData.make() + " " + aiData.model()
                  + " (" + aiData.trim() + "):";

            return new AssistantResult(successMsg, products, false);

        } catch (Exception e) {
            e.printStackTrace();
            return new AssistantResult(
                "Hey! 👋 I'm your Virtual Mechanic at Mad Garage. It looks like I had a small hiccup. " +
                "Could you tell me your vehicle's Year, Make & Model so I can find the right parts for you?",
                new ArrayList<>(), true
            );
        }
    }

    // ── Helper: is this message a simple greeting / small-talk? ────────────────
    private boolean isGreeting(String text) {
        String lower = text.toLowerCase().replaceAll("[^a-z ]", "").trim();
        if (GREETINGS.contains(lower)) return true;
        // Also catch very short messages with no numbers (unlikely to be a car query)
        return lower.length() <= 4 && !lower.matches(".*\\d.*");
    }
}