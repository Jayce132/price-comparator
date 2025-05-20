package com.example.pricecomparator.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "product_price",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"store_id","product_id","price_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductPrice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false) @JoinColumn(name = "store_id")
    private Store store;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency;      // “RON”

    @Column(name = "price_date", nullable = false)
    private LocalDate date;       // snapshot date
}
