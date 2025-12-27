package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrderId(Long orderId);

    List<OrderDetail> findByProductId(Long productId);

    // Top sản phẩm bán chạy
    @Query("""
                SELECT
                    p.id,
                    p.name,
                    SUM(od.quantity) as totalSold,
                    SUM(od.subtotal) as totalRevenue
                FROM OrderDetail od
                JOIN od.product p
                JOIN od.order o
                WHERE o.status = 'COMPLETED'
                AND o.createdAt BETWEEN :startDate AND :endDate
                AND o.deletedAt IS NULL
                GROUP BY p.id, p.name
                ORDER BY totalSold DESC
            """)
    List<Object[]> getTopSellingProducts(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            org.springframework.data.domain.Pageable pageable);

    // Sản phẩm thường mua cùng
    @Query("""
                SELECT
                    p2.id,
                    p2.name,
                    COUNT(DISTINCT od2.order.id) as frequency
                FROM OrderDetail od1
                JOIN od1.order o1
                JOIN OrderDetail od2 ON od2.order.id = o1.id
                JOIN od2.product p2
                WHERE od1.product.id = :productId
                AND od2.product.id != :productId
                AND o1.status = 'COMPLETED'
                AND o1.deletedAt IS NULL
                GROUP BY p2.id, p2.name
                ORDER BY frequency DESC
            """)
    List<Object[]> getFrequentlyBoughtTogether(
            @Param("productId") Long productId,
            org.springframework.data.domain.Pageable pageable);
}
