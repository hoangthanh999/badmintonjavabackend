package com.hoangthanhhong.badminton.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    // Track online users per chat room
    private final Map<Long, Map<Long, String>> onlineUsers = new ConcurrentHashMap<>();

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("WebSocket connection established. Session ID: {}", sessionId);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {
            Long userId = (Long) sessionAttributes.get("userId");
            Long roomId = (Long) sessionAttributes.get("roomId");

            if (userId != null && roomId != null) {
                // Remove user from online users
                Map<Long, String> roomUsers = onlineUsers.get(roomId);
                if (roomUsers != null) {
                    roomUsers.remove(userId);

                    // Broadcast user left
                    messagingTemplate.convertAndSend(
                            "/topic/chat-room/" + roomId + "/users",
                            Map.of(
                                    "type", "USER_LEFT",
                                    "userId", userId,
                                    "onlineCount", roomUsers.size()));
                }

                log.info("User {} disconnected from chat room {}. Session ID: {}",
                        userId, roomId, sessionId);
            }
        }

        log.info("WebSocket connection closed. Session ID: {}", sessionId);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = headerAccessor.getDestination();
        String sessionId = headerAccessor.getSessionId();

        log.info("User subscribed to: {}. Session ID: {}", destination, sessionId);

        // Extract room ID from destination
        if (destination != null && destination.startsWith("/topic/chat-room/")) {
            try {
                String[] parts = destination.split("/");
                Long roomId = Long.parseLong(parts[3]);

                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    Long userId = (Long) sessionAttributes.get("userId");

                    if (userId != null) {
                        // Add user to online users
                        onlineUsers.computeIfAbsent(roomId, k -> new ConcurrentHashMap<>())
                                .put(userId, sessionId);

                        // Broadcast user joined
                        messagingTemplate.convertAndSend(
                                "/topic/chat-room/" + roomId + "/users",
                                Map.of(
                                        "type", "USER_JOINED",
                                        "userId", userId,
                                        "onlineCount", onlineUsers.get(roomId).size()));
                    }
                }
            } catch (Exception e) {
                log.error("Error parsing room ID from destination: {}", destination, e);
            }
        }
    }

    @EventListener
    public void handleUnsubscribeEvent(SessionUnsubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();

        log.info("User unsubscribed. Session ID: {}", sessionId);
    }

    // ===== HELPER METHODS =====

    public Map<Long, String> getOnlineUsers(Long roomId) {
        return onlineUsers.getOrDefault(roomId, new ConcurrentHashMap<>());
    }

    public int getOnlineUserCount(Long roomId) {
        return getOnlineUsers(roomId).size();
    }

    public boolean isUserOnline(Long roomId, Long userId) {
        return getOnlineUsers(roomId).containsKey(userId);
    }
}
