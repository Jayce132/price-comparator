package com.example.pricecomparator.repository;

import com.example.pricecomparator.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByExternalId(String externalId);
}
