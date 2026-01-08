package com.hoangthanhhong.badminton.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hoangthanhhong.badminton.enums.AddressType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressResponse {

    private Long id;

    private Long userId;

    private AddressType type;

    private String receiverName;

    private String receiverPhone;

    private String addressLine1;

    private String addressLine2;

    private String ward;

    private String district;

    private String city;

    private String postalCode;

    private String country;

    private Boolean isDefault;

    private Double latitude;

    private Double longitude;

    private Boolean isActive;

    private String notes;

    private String label;

    private String fullAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
