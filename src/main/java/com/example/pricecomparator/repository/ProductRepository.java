package com.example.pricecomparator.repository;

import com.example.pricecomparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
