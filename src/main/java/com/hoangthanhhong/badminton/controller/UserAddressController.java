package com.hoangthanhhong.badminton.controller;

import com.hoangthanhhong.badminton.dto.request.UserAddressRequest;
import com.hoangthanhhong.badminton.dto.response.UserAddressResponse;
import com.hoangthanhhong.badminton.enums.AddressType;
import com.hoangthanhhong.badminton.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {

    private final UserAddressService addressService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> createAddress(
            @Valid @RequestBody UserAddressRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserAddressResponse response = addressService.createAddress(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> getAddress(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        addressService.validateAddressOwnership(id, userId);
        UserAddressResponse response = addressService.getAddressById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-addresses")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserAddressResponse>> getMyAddresses(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<UserAddressResponse> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/my-addresses/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserAddressResponse>> getMyActiveAddresses(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<UserAddressResponse> addresses = addressService.getActiveAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    @GetMapping("/my-addresses/default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> getDefaultAddress(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserAddressResponse response = addressService.getDefaultAddress(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-addresses/type/{type}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserAddressResponse>> getAddressesByType(
            @PathVariable AddressType type,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        List<UserAddressResponse> addresses = addressService.getAddressesByType(userId, type);
        return ResponseEntity.ok(addresses);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressRequest request,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserAddressResponse response = addressService.updateAddress(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/set-default")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> setDefaultAddress(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserAddressResponse response = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/coordinates")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserAddressResponse> updateCoordinates(
            @PathVariable Long id,
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        UserAddressResponse response = addressService.updateCoordinates(id, latitude, longitude, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long id,
            Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        addressService.deleteAddress(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-addresses/count")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Long> countMyAddresses(Authentication authentication) {
        Long userId = getUserIdFromAuth(authentication);
        Long count = addressService.countAddressesByUser(userId);
        return ResponseEntity.ok(count);
    }

    private Long getUserIdFromAuth(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
