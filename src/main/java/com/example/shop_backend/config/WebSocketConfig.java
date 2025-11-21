package com.example.shop_backend.config;

import com.example.shop_backend.model.User;
import com.example.shop_backend.repository.UserRepository;
import com.example.shop_backend.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
        
        System.out.println("✅ Message Broker configured");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS();
        
        System.out.println("✅ STOMP Endpoint registered: /ws-chat");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    
                    System.out.println("🔍 WebSocket CONNECT");
                    System.out.println("🔑 Token: " + (token != null ? "Present" : "NULL"));
                    
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        
                        if (jwtUtils.validateToken(token)) {
                            String email = jwtUtils.getEmailFromToken(token);
                            String role = jwtUtils.getRoleFromToken(token);
                            
                            System.out.println("✅ Token valid - Email: " + email);
                            
                            // ✅ LOAD USER TỪ DATABASE
                            User user = userRepository.findByEmail(email).orElse(null);
                            
                            if (user != null) {
                                System.out.println("✅ User found: " + user.getEmail() + ", ID: " + user.getId() + ", Role: " + user.getRole());
                                
                                // ✅ SET USER ENTITY LÀM PRINCIPAL
                                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                                UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                        user,  // ✅ Principal là User entity (không phải String)
                                        null,
                                        Collections.singletonList(authority)
                                    );
                                
                                accessor.setUser(authentication);
                                System.out.println("✅ Set User entity vào WebSocket session");
                            } else {
                                System.out.println("❌ User not found: " + email);
                            }
                        } else {
                            System.out.println("❌ Invalid token");
                        }
                    }
                }
                
                return message;
            }
        });
    }
}