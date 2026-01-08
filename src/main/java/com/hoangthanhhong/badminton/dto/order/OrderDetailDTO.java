// File: OrderDetailDTO.java
package com.hoangthanhhong.badminton.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailDTO {
    private Long id;

    // Product
    private Long productId;
    private String productName;
    private String productImage;
    private String productSku;

    // Quantity & Price
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
    private Double discount;
    private Double total;
}
