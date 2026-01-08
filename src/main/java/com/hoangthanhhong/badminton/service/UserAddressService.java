package com.hoangthanhhong.badminton.service;

import com.hoangthanhhong.badminton.dto.request.UserAddressRequest;
import com.hoangthanhhong.badminton.dto.response.UserAddressResponse;
import com.hoangthanhhong.badminton.enums.AddressType;

import java.util.List;

public interface UserAddressService {

    // === CREATE ===
    UserAddressResponse createAddress(UserAddressRequest request, Long userId);

    // === READ ===
    UserAddressResponse getAddressById(Long id);

    List<UserAddressResponse> getAddressesByUserId(Long userId);

    List<UserAddressResponse> getActiveAddressesByUserId(Long userId);

    UserAddressResponse getDefaultAddress(Long userId);

    List<UserAddressResponse> getAddressesByType(Long userId, AddressType type);

    // === UPDATE ===
    UserAddressResponse updateAddress(Long id, UserAddressRequest request, Long userId);

    UserAddressResponse setDefaultAddress(Long addressId, Long userId);

    UserAddressResponse updateCoordinates(Long addressId, Double latitude, Double longitude, Long userId);

    // === DELETE ===
    void deleteAddress(Long id, Long userId);

    // === STATISTICS ===
    Long countAddressesByUser(Long userId);

    Long countActiveAddressesByUser(Long userId);

    boolean hasDefaultAddress(Long userId);

    // === VALIDATION ===
    boolean isAddressOwner(Long addressId, Long userId);

    void validateAddressOwnership(Long addressId, Long userId);
}
