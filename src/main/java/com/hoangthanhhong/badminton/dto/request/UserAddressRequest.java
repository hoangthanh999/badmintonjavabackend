package com.hoangthanhhong.badminton.dto.request;

import com.hoangthanhhong.badminton.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAddressRequest {

    private AddressType type;

    @NotBlank(message = "Tên người nhận không được để trống")
    @Size(max = 100, message = "Tên người nhận không được quá 100 ký tự")
    private String receiverName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(\\+84|0)[0-9]{9}$", message = "Số điện thoại không hợp lệ")
    private String receiverPhone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String addressLine1;

    private String addressLine2;

    private String ward;

    private String district;

    @NotBlank(message = "Thành phố không được để trống")
    private String city;

    private String postalCode;

    @Builder.Default
    private String country = "Vietnam";

    private Boolean isDefault;

    private Double latitude;

    private Double longitude;

    private String notes;

    private String label;
}
