package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.chat.ChatMessageDTO;
import com.hoangthanhhong.badminton.dto.chat.TypingIndicatorDTO;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatMessageRequest;
import com.hoangthanhhong.badminton.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;

    /**
     * Send message via WebSocket
     * Client sends to: /app/chat.sendMessage
     * Broadcast to: /topic/chat-room/{roomId}
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(
            @Payload CreateChatMessageRequest request,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            ChatMessageDTO message = chatService.sendMessage(request, userId);
            log.info("Message sent via WebSocket by user: {}", userId);
        } catch (Exception e) {
            log.error("Error sending message via WebSocket", e);
        }
    }

    /**
     * Typing indicator
     * Client sends to: /app/chat.typing/{roomId}
     * Broadcast to: /topic/chat-room/{roomId}/typing
     */
    @MessageMapping("/chat.typing/{roomId}")
    @SendTo("/topic/chat-room/{roomId}/typing")
    public TypingIndicatorDTO sendTypingIndicator(
            @DestinationVariable Long roomId,
            @Payload TypingIndicatorDTO indicator,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            indicator.setUserId(userId);
            indicator.setChatRoomId(roomId);

            chatService.sendTypingIndicator(roomId, userId, indicator.getIsTyping());

            return indicator;
        } catch (Exception e) {
            log.error("Error sending typing indicator", e);
            return null;
        }
    }

    /**
     * User joined chat room
     * Client sends to: /app/chat.join/{roomId}
     * Broadcast to: /topic/chat-room/{roomId}/users
     */
    @MessageMapping("/chat.join/{roomId}")
    @SendTo("/topic/chat-room/{roomId}/users")
    public String userJoined(
            @DestinationVariable Long roomId,
            Principal principal,
            SimpMessageHeaderAccessor headerAccessor) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);

            // Store user info in WebSocket session
            headerAccessor.getSessionAttributes().put("userId", userId);
            headerAccessor.getSessionAttributes().put("roomId", roomId);

            log.info("User {} joined chat room {}", userId, roomId);
            return "User " + userId + " joined";
        } catch (Exception e) {
            log.error("Error in user join", e);
            return null;
        }
    }

    /**
     * User left chat room
     * Client sends to: /app/chat.leave/{roomId}
     * Broadcast to: /topic/chat-room/{roomId}/users
     */
    @MessageMapping("/chat.leave/{roomId}")
    @SendTo("/topic/chat-room/{roomId}/users")
    public String userLeft(
            @DestinationVariable Long roomId,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            log.info("User {} left chat room {}", userId, roomId);
            return "User " + userId + " left";
        } catch (Exception e) {
            log.error("Error in user leave", e);
            return null;
        }
    }

    /**
     * Subscribe to chat room
     * Client subscribes to: /topic/chat-room/{roomId}
     */
    @SubscribeMapping("/chat-room/{roomId}")
    public void subscribeToChatRoom(
            @DestinationVariable Long roomId,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            log.info("User {} subscribed to chat room {}", userId, roomId);
        } catch (Exception e) {
            log.error("Error in subscription", e);
        }
    }

    /**
     * Mark messages as read
     * Client sends to: /app/chat.read/{roomId}
     */
    @MessageMapping("/chat.read/{roomId}")
    public void markAsRead(
            @DestinationVariable Long roomId,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            chatService.markAsRead(roomId, userId);
            log.info("User {} marked chat room {} as read", userId, roomId);
        } catch (Exception e) {
            log.error("Error marking as read", e);
        }
    }

    /**
     * React to message
     * Client sends to: /app/chat.react/{messageId}
     */
    @MessageMapping("/chat.react/{messageId}")
    public void reactToMessage(
            @DestinationVariable Long messageId,
            @Payload String emoji,
            Principal principal) {

        try {
            Long userId = extractUserIdFromPrincipal(principal);
            chatService.reactToMessage(messageId, emoji, userId);
            log.info("User {} reacted to message {} with {}", userId, messageId, emoji);
        } catch (Exception e) {
            log.error("Error reacting to message", e);
        }
    }

    // ===== HELPER METHODS =====

    private Long extractUserIdFromPrincipal(Principal principal) {
        // Extract user ID from principal
        // This depends on your authentication implementation
        if (principal != null) {
            // Example: parse from principal name
            try {
                return Long.parseLong(principal.getName());
            } catch (NumberFormatException e) {
                // If principal name is email, you need to look up user
                log.warn("Cannot parse userId from principal: {}", principal.getName());
            }
        }
        throw new IllegalStateException("User not authenticated");
    }
}
