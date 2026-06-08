package com.madgarage.api.config;

import com.madgarage.api.services.JwtService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Intercepts the STOMP CONNECT frame to authenticate WebSocket connections.
 *
 * Strategy:
 *  - Mobile (MadGarageApp): reads "Authorization: Bearer <jwt>" from STOMP headers
 *  - Web (MadGarageWeb):    the SockJS HTTP handshake already carries the mg_auth
 *                            HttpOnly cookie, which JwtFilter validates at the HTTP
 *                            layer before the STOMP session is even established.
 *                            So for web clients, authentication is already done; we
 *                            just pass through.
 */
@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private static final org.slf4j.Logger log =
            org.slf4j.LoggerFactory.getLogger(WebSocketAuthChannelInterceptor.class);

    private final JwtService jwtService;

    public WebSocketAuthChannelInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            // If there is already an authenticated user (web via cookie), skip
            if (accessor.getUser() != null) {
                return message;
            }

            // Try to read Authorization header from STOMP CONNECT (mobile)
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7).trim();
                try {
                    String email = jwtService.extractEmail(token);
                    String role  = jwtService.extractRole(token);

                    if (email != null && role != null) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(
                                        email, null,
                                        Collections.singletonList(new SimpleGrantedAuthority(role))
                                );
                        accessor.setUser(auth);
                        log.debug("[WS] Authenticated STOMP connection for: {}", email);
                    }
                } catch (Exception e) {
                    log.warn("[WS] Invalid JWT in STOMP CONNECT headers: {}", e.getMessage());
                    // Return null to reject the connection
                    return null;
                }
            }
        }

        return message;
    }
}
