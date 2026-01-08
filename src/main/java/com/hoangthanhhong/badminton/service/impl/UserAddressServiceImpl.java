package com.hoangthanhhong.badminton.service.impl;

import com.hoangthanhhong.badminton.dto.request.UserAddressRequest;
import com.hoangthanhhong.badminton.dto.response.UserAddressResponse;
import com.hoangthanhhong.badminton.entity.User;
import com.hoangthanhhong.badminton.entity.UserAddress;
import com.hoangthanhhong.badminton.enums.AddressType;
import com.hoangthanhhong.badminton.exception.ResourceNotFoundException;
import com.hoangthanhhong.badminton.mapper.UserAddressMapper;
import com.hoangthanhhong.badminton.repository.UserAddressRepository;
import com.hoangthanhhong.badminton.repository.UserRepository;
import com.hoangthanhhong.badminton.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository addressRepository;
    private final UserRepository userRepository;
    private final UserAddressMapper addressMapper;

    @Override
    public UserAddressResponse createAddress(UserAddressRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        // Nếu đây là địa chỉ đầu tiên, tự động set làm default
        boolean isFirstAddress = addressRepository.countByUserId(userId) == 0;

        UserAddress address = UserAddress.builder()
                .user(user)
                .type(request.getType())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .addressLine1(request.getAddressLine1())
                .addressLine2(request.getAddressLine2())
                .ward(request.getWard())
                .district(request.getDistrict())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .country(request.getCountry() != null ? request.getCountry() : "Vietnam")
                .isDefault(isFirstAddress || request.getIsDefault())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .notes(request.getNotes())
                .label(request.getLabel())
                .isActive(true)
                .build();

        // Nếu set làm default, unset các address khác
        if (address.getIsDefault()) {
            addressRepository.unsetAllDefaultByUserId(userId);
        }

        address = addressRepository.save(address);
        log.info("Created address {} for user {}", address.getId(), userId);

        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getAddressById(Long id) {
        UserAddress address = addressRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getActiveAddressesByUserId(Long userId) {
        return addressRepository.findActiveByUserId(userId).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserAddressResponse getDefaultAddress(Long userId) {
        UserAddress address = addressRepository.findDefaultByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ mặc định"));
        return addressMapper.toResponse(address);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserAddressResponse> getAddressesByType(Long userId, AddressType type) {
        return addressRepository.findByUserIdAndType(userId, type).stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserAddressResponse updateAddress(Long id, UserAddressRequest request, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        // Update fields
        address.setType(request.getType());
        address.setReceiverName(request.getReceiverName());
        address.setReceiverPhone(request.getReceiverPhone());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setWard(request.getWard());
        address.setDistrict(request.getDistrict());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setLatitude(request.getLatitude());
        address.setLongitude(request.getLongitude());
        address.setNotes(request.getNotes());
        address.setLabel(request.getLabel());

        // Handle default address change
        if (request.getIsDefault() && !address.getIsDefault()) {
            addressRepository.unsetAllDefaultByUserId(userId);
            address.setIsDefault(true);
        }

        address = addressRepository.save(address);
        log.info("Updated address {} for user {}", id, userId);

        return addressMapper.toResponse(address);
    }

    @Override
    public UserAddressResponse setDefaultAddress(Long addressId, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        // Unset all default addresses
        addressRepository.unsetAllDefaultByUserId(userId);

        // Set new default
        address.setIsDefault(true);
        address = addressRepository.save(address);

        log.info("Set address {} as default for user {}", addressId, userId);
        return addressMapper.toResponse(address);
    }

    @Override
    public UserAddressResponse updateCoordinates(Long addressId, Double latitude, Double longitude, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        address.setLatitude(latitude);
        address.setLongitude(longitude);
        address = addressRepository.save(address);

        log.info("Updated coordinates for address {}", addressId);
        return addressMapper.toResponse(address);
    }

    @Override
    public void deleteAddress(Long id, Long userId) {
        UserAddress address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy địa chỉ"));

        // Nếu xóa địa chỉ mặc định, set địa chỉ khác làm mặc định
        if (address.getIsDefault()) {
            List<UserAddress> otherAddresses = addressRepository.findByUserId(userId);
            if (otherAddresses.size() > 1) {
                UserAddress newDefault = otherAddresses.stream()
                        .filter(a -> !a.getId().equals(id))
                        .findFirst()
                        .orElse(null);
                if (newDefault != null) {
                    newDefault.setIsDefault(true);
                    addressRepository.save(newDefault);
                }
            }
        }

        address.softDelete();
        addressRepository.save(address);
        log.info("Soft deleted address {} for user {}", id, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countAddressesByUser(Long userId) {
        return addressRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countActiveAddressesByUser(Long userId) {
        return addressRepository.countActiveByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasDefaultAddress(Long userId) {
        return addressRepository.existsDefaultAddressByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isAddressOwner(Long addressId, Long userId) {
        return addressRepository.findByIdAndUserId(addressId, userId).isPresent();
    }

    @Override
    public void validateAddressOwnership(Long addressId, Long userId) {
        if (!isAddressOwner(addressId, userId)) {
            throw new IllegalStateException("Bạn không có quyền truy cập địa chỉ này");
        }
    }
}