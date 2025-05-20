package com.example.pricecomparator.repository;

import com.example.pricecomparator.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
}
