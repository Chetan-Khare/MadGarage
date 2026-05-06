package com.madgarage.api.repository;

import com.madgarage.api.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByUserIdOrderByCreatedAtAsc(Long userId);
    Page<ChatMessage> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
