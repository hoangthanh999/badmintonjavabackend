package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.chat.ChatMessageDTO;
import com.hoangthanhhong.badminton.dto.chat.ChatRoomDTO;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatMessageRequest;
import com.hoangthanhhong.badminton.dto.request.chat.CreateChatRoomRequest;
import com.hoangthanhhong.badminton.dto.response.ApiResponse;
import com.hoangthanhhong.badminton.dto.response.PageResponse;
import com.hoangthanhhong.badminton.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "Chat API")
@SecurityRequirement(name = "bearerAuth")
public class ChatController {

    private final ChatService chatService;

    // ===== CHAT ROOM ENDPOINTS =====

    @PostMapping("/rooms")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Create chat room", description = "Create a new chat room")
    public ResponseEntity<EntityModel<ChatRoomDTO>> createChatRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ChatRoomDTO chatRoom = chatService.createChatRoom(request, userId);

        EntityModel<ChatRoomDTO> model = EntityModel.of(chatRoom);
        model.add(linkTo(methodOn(ChatController.class).getChatRoomById(chatRoom.getId(), userDetails)).withSelfRel());
        model.add(linkTo(methodOn(ChatController.class).getUserChatRooms(0, 20, userDetails)).withRel("all-rooms"));
        model.add(linkTo(methodOn(ChatController.class).getChatRoomMessages(chatRoom.getId(), 0, 50, userDetails))
                .withRel("messages"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(model);
    }

    @GetMapping("/rooms/{id}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get chat room by ID", description = "Get chat room details by ID")
    public ResponseEntity<EntityModel<ChatRoomDTO>> getChatRoomById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ChatRoomDTO chatRoom = chatService.getChatRoomById(id, userId);

        EntityModel<ChatRoomDTO> model = EntityModel.of(chatRoom);
        model.add(linkTo(methodOn(ChatController.class).getChatRoomById(id, userDetails)).withSelfRel());
        model.add(
                linkTo(methodOn(ChatController.class).getChatRoomMessages(id, 0, 50, userDetails)).withRel("messages"));
        model.add(linkTo(methodOn(ChatController.class).leaveChatRoom(id, userDetails)).withRel("leave"));

        if (chatRoom.getType().toString().equals("GROUP")) {
            model.add(linkTo(methodOn(ChatController.class).addParticipant(id, null, userDetails))
                    .withRel("add-participant"));
        }

        return ResponseEntity.ok(model);
    }

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get user chat rooms", description = "Get all chat rooms for current user")
    public ResponseEntity<CollectionModel<EntityModel<ChatRoomDTO>>> getUserChatRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("lastMessageAt").descending());
        Page<ChatRoomDTO> chatRooms = chatService.getUserChatRooms(userId, pageable);

        List<EntityModel<ChatRoomDTO>> models = chatRooms.getContent().stream()
                .map(room -> {
                    EntityModel<ChatRoomDTO> model = EntityModel.of(room);
                    model.add(linkTo(methodOn(ChatController.class).getChatRoomById(room.getId(), userDetails))
                            .withSelfRel());
                    model.add(
                            linkTo(methodOn(ChatController.class).getChatRoomMessages(room.getId(), 0, 50, userDetails))
                                    .withRel("messages"));
                    return model;
                })
                .collect(Collectors.toList());

        CollectionModel<EntityModel<ChatRoomDTO>> collectionModel = CollectionModel.of(models);
        collectionModel
                .add(linkTo(methodOn(ChatController.class).getUserChatRooms(page, size, userDetails)).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }

    @GetMapping("/rooms/direct/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get or create direct chat", description = "Get existing or create new direct chat with user")
    public ResponseEntity<EntityModel<ChatRoomDTO>> getOrCreateDirectChat(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = getUserIdFromUserDetails(userDetails);
        ChatRoomDTO chatRoom = chatService.getOrCreateDirectChat(currentUserId, userId);

        EntityModel<ChatRoomDTO> model = EntityModel.of(chatRoom);
        model.add(linkTo(methodOn(ChatController.class).getChatRoomById(chatRoom.getId(), userDetails)).withSelfRel());
        model.add(linkTo(methodOn(ChatController.class).getChatRoomMessages(chatRoom.getId(), 0, 50, userDetails))
                .withRel("messages"));

        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/rooms/{id}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Delete chat room", description = "Delete chat room (owner only)")
    public ResponseEntity<ApiResponse<Void>> deleteChatRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.deleteChatRoom(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Chat room deleted successfully")
                        .build());
    }

    @PostMapping("/rooms/{id}/leave")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Leave chat room", description = "Leave a chat room")
    public ResponseEntity<ApiResponse<Void>> leaveChatRoom(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.leaveChatRoom(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Left chat room successfully")
                        .build());
    }

    // ===== PARTICIPANT ENDPOINTS =====

    @PostMapping("/rooms/{roomId}/participants/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Add participant", description = "Add participant to chat room")
    public ResponseEntity<ApiResponse<Void>> addParticipant(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = getUserIdFromUserDetails(userDetails);
        chatService.addParticipant(roomId, userId, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Participant added successfully")
                        .build());
    }

    @DeleteMapping("/rooms/{roomId}/participants/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Remove participant", description = "Remove participant from chat room")
    public ResponseEntity<ApiResponse<Void>> removeParticipant(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = getUserIdFromUserDetails(userDetails);
        chatService.removeParticipant(roomId, userId, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Participant removed successfully")
                        .build());
    }

    @PatchMapping("/rooms/{roomId}/participants/{userId}/role")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Update participant role", description = "Update participant role in chat room")
    public ResponseEntity<ApiResponse<Void>> updateParticipantRole(
            @PathVariable Long roomId,
            @PathVariable Long userId,
            @RequestParam String role,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long currentUserId = getUserIdFromUserDetails(userDetails);
        chatService.updateParticipantRole(roomId, userId, role, currentUserId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Participant role updated successfully")
                        .build());
    }

    // ===== MESSAGE ENDPOINTS =====

    @PostMapping("/messages")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Send message", description = "Send a message in chat room")
    public ResponseEntity<EntityModel<ChatMessageDTO>> sendMessage(
            @Valid @RequestBody CreateChatMessageRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ChatMessageDTO message = chatService.sendMessage(request, userId);

        EntityModel<ChatMessageDTO> model = EntityModel.of(message);
        model.add(linkTo(methodOn(ChatController.class).getChatRoomMessages(
                request.getChatRoomId(), 0, 50, userDetails)).withRel("chat-room-messages"));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(model);
    }

    @GetMapping("/rooms/{roomId}/messages")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Get chat room messages", description = "Get messages in a chat room")
    public ResponseEntity<PageResponse<ChatMessageDTO>> getChatRoomMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
        Page<ChatMessageDTO> messages = chatService.getChatRoomMessages(roomId, userId, pageable);

        PageResponse<ChatMessageDTO> response = PageResponse.<ChatMessageDTO>builder()
                .content(messages.getContent())
                .pageNumber(messages.getNumber())
                .pageSize(messages.getSize())
                .totalElements(messages.getTotalElements())
                .totalPages(messages.getTotalPages())
                .last(messages.isLast())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms/{roomId}/messages/search")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Search messages", description = "Search messages in chat room")
    public ResponseEntity<ApiResponse<List<ChatMessageDTO>>> searchMessages(
            @PathVariable Long roomId,
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        List<ChatMessageDTO> messages = chatService.searchMessages(roomId, query, userId);

        return ResponseEntity.ok(
                ApiResponse.<List<ChatMessageDTO>>builder()
                        .success(true)
                        .message("Messages found")
                        .data(messages)
                        .build());
    }

    @PutMapping("/messages/{id}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Edit message", description = "Edit a message")
    public ResponseEntity<EntityModel<ChatMessageDTO>> editMessage(
            @PathVariable Long id,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ChatMessageDTO message = chatService.editMessage(id, content, userId);

        EntityModel<ChatMessageDTO> model = EntityModel.of(message);
        return ResponseEntity.ok(model);
    }

    @DeleteMapping("/messages/{id}")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Delete message", description = "Delete a message")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.deleteMessage(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Message deleted successfully")
                        .build());
    }

    // ===== MESSAGE ACTIONS =====

    @PostMapping("/messages/{id}/pin")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Pin message", description = "Pin a message in chat room")
    public ResponseEntity<ApiResponse<Void>> pinMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.pinMessage(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Message pinned successfully")
                        .build());
    }

    @DeleteMapping("/messages/{id}/pin")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Unpin message", description = "Unpin a message")
    public ResponseEntity<ApiResponse<Void>> unpinMessage(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.unpinMessage(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Message unpinned successfully")
                        .build());
    }

    @PostMapping("/messages/{id}/react")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "React to message", description = "Add reaction to message")
    public ResponseEntity<ApiResponse<Void>> reactToMessage(
            @PathVariable Long id,
            @RequestParam String emoji,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.reactToMessage(id, emoji, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Reaction added successfully")
                        .build());
    }

    @DeleteMapping("/messages/{id}/react")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Remove reaction", description = "Remove reaction from message")
    public ResponseEntity<ApiResponse<Void>> removeReaction(
            @PathVariable Long id,
            @RequestParam String emoji,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.removeReaction(id, emoji, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Reaction removed successfully")
                        .build());
    }

    // ===== READ RECEIPTS =====

    @PostMapping("/rooms/{roomId}/read")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Mark as read", description = "Mark all messages in chat room as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.markAsRead(roomId, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Marked as read successfully")
                        .build());
    }

    @PostMapping("/messages/{id}/read")
    @PreAuthorize("hasAnyRole('USER', 'STAFF', 'MANAGER', 'ADMIN')")
    @Operation(summary = "Mark message as read", description = "Mark specific message as read")
    public ResponseEntity<ApiResponse<Void>> markMessageAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        chatService.markMessageAsRead(id, userId);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message("Message marked as read")
                        .build());
    }

    // ===== HELPER METHODS =====

    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // Implement based on your UserDetails implementation
        // This is a placeholder
        return 1L; // Replace with actual implementation
    }
}
