package com.madgarage.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configures the STOMP over WebSocket message broker.
 *
 * Endpoint:   /ws  (clients connect here)
 * Broker:     /topic/** for broadcast topics
 * App prefix: /app for @MessageMapping handlers (reserved for future use)
 *
 * Authentication is handled upstream by WebSocketAuthChannelInterceptor,
 * which reads the JWT from the STOMP CONNECT headers (mobile) or the
 * mg_auth HttpOnly cookie already validated by JwtFilter (web).
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthChannelInterceptor authChannelInterceptor;

    public WebSocketConfig(WebSocketAuthChannelInterceptor authChannelInterceptor) {
        this.authChannelInterceptor = authChannelInterceptor;
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Enables a simple in-memory broker to carry messages to clients subscribed to /topic/**
        config.enableSimpleBroker("/topic", "/queue");
        // Prefix for @MessageMapping methods (not used yet, but good practice)
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-private destinations (e.g. /user/queue/errors)
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                // Mirror the CORS origins from SecurityConfig exactly
                .setAllowedOriginPatterns(
                        "https://www.madgarage.in",
                        "https://madgarage.in",
                        "http://localhost:3000",
                        "http://localhost:8081",
                        "http://localhost:*"
                )
                // SockJS fallback for browsers/environments that block raw WebSockets
                .withSockJS();
    }
}
