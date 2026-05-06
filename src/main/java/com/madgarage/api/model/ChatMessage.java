package com.madgarage.api.model;

import com.madgarage.api.converter.GzipStringConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    public enum SenderType { USER, AI }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Chat message text — stored GZip-compressed (MEDIUMBLOB).
     * Typical savings: 50–80% vs plain TEXT. Decompressed transparently on read.
     */
    @Convert(converter = GzipStringConverter.class)
    @Column(columnDefinition = "MEDIUMBLOB")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SenderType sender;

    /**
     * Intentionally NOT stored — images are sent to Gemini AI transiently
     * and discarded to avoid cloud storage costs. Always null in the DB.
     */
    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
