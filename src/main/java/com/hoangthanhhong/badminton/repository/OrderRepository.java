package com.hoangthanhhong.badminton.repository;

import com.badminton.entity.Order;
import com.badminton.enums.OrderStatus;
import com.badminton.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // === BASIC QUERIES ===

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByUserId(Long userId);

    Page<Order> findByUserId(Long userId, Pageable pageable);

    List<Order> findByStatus(OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByIdAndUserId(Long id, Long userId);

    // === COMPLEX QUERIES ===

    // 1. Tìm kiếm đơn hàng với nhiều điều kiện
    @Query("""
                SELECT o FROM Order o
                WHERE (:userId IS NULL OR o.user.id = :userId)
                AND (:status IS NULL OR o.status = :status)
                AND (:paymentStatus IS NULL OR o.paymentStatus = :paymentStatus)
                AND (:startDate IS NULL OR o.createdAt >= :startDate)
                AND (:endDate IS NULL OR o.createdAt <= :endDate)
                AND (:searchTerm IS NULL OR
                     LOWER(o.orderCode) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                     LOWER(o.shippingName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR
                     LOWER(o.shippingPhone) LIKE LOWER(CONCAT('%', :searchTerm, '%')))
                AND o.deletedAt IS NULL
                ORDER BY o.createdAt DESC
            """)
    Page<Order> searchOrders(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status,
            @Param("paymentStatus") PaymentStatus paymentStatus,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // 2. Thống kê doanh thu theo tháng
    @Query("""
                SELECT
                    FUNCTION('YEAR', o.createdAt) as year,
                    FUNCTION('MONTH', o.createdAt) as month,
                    COUNT(o) as totalOrders,
                    SUM(o.finalAmount) as revenue,
                    AVG(o.finalAmount) as avgOrderValue
                FROM Order o
                WHERE o.status = 'COMPLETED'
                AND o.createdAt BETWEEN :startDate AND :endDate
                AND o.deletedAt IS NULL
                GROUP BY FUNCTION('YEAR', o.createdAt), FUNCTION('MONTH', o.createdAt)
                ORDER BY year DESC, month DESC
            """)
    List<Object[]> getRevenueByMonth(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 3. Top khách hàng mua nhiều nhất
    @Query("""
                SELECT
                    u.id,
                    u.name,
                    u.email,
                    COUNT(o) as totalOrders,
                    SUM(o.finalAmount) as totalSpent,
                    AVG(o.finalAmount) as avgOrderValue
                FROM Order o
                JOIN o.user u
                WHERE o.status = 'COMPLETED'
                AND o.createdAt BETWEEN :startDate AND :endDate
                AND o.deletedAt IS NULL
                GROUP BY u.id, u.name, u.email
                ORDER BY totalSpent DESC
            """)
    List<Object[]> getTopCustomers(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // 4. Đơn hàng cần xử lý
    @Query("""
                SELECT o FROM Order o
                WHERE o.status IN ('PENDING', 'CONFIRMED')
                AND o.deletedAt IS NULL
                ORDER BY o.createdAt ASC
            """)
    List<Order> findPendingOrders();

    // 5. Đơn hàng quá hạn chưa thanh toán
    @Query("""
                SELECT o FROM Order o
                WHERE o.status = 'PENDING'
                AND o.paymentStatus = 'PENDING'
                AND o.createdAt < :expiryTime
                AND o.deletedAt IS NULL
            """)
    List<Order> findExpiredOrders(@Param("expiryTime") LocalDateTime expiryTime);

    // 6. Thống kê theo trạng thái
    @Query("""
                SELECT o.status, COUNT(o)
                FROM Order o
                WHERE o.createdAt BETWEEN :startDate AND :endDate
                AND o.deletedAt IS NULL
                GROUP BY o.status
            """)
    List<Object[]> getOrderStatsByStatus(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 7. Tổng doanh thu
    @Query("""
                SELECT SUM(o.finalAmount)
                FROM Order o
                WHERE o.status = 'COMPLETED'
                AND o.createdAt BETWEEN :startDate AND :endDate
                AND o.deletedAt IS NULL
            """)
    Double getTotalRevenue(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // 8. Đếm đơn hàng theo user
    @Query("SELECT COUNT(o) FROM Order o WHERE o.user.id = :userId AND o.deletedAt IS NULL")
    Long countByUserId(@Param("userId") Long userId);

    // 9. Đơn hàng gần đây của user
    @Query("""
                SELECT o FROM Order o
                WHERE o.user.id = :userId
                AND o.deletedAt IS NULL
                ORDER BY o.createdAt DESC
            """)
    List<Order> findRecentOrdersByUserId(@Param("userId") Long userId, Pageable pageable);

    // 10. Kiểm tra user đã mua sản phẩm chưa
    @Query("""
                SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
                FROM Order o
                JOIN o.orderDetails od
                WHERE o.user.id = :userId
                AND od.product.id = :productId
                AND o.status = 'COMPLETED'
                AND o.deletedAt IS NULL
            """)
    boolean hasUserPurchasedProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId);
}
