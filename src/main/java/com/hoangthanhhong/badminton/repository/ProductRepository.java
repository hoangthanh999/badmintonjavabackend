package com.hoangthanhhong.badminton.repository;

import com.hoangthanhhong.badminton.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}