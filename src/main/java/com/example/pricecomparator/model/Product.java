package com.example.pricecomparator.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @Column(name = "external_id", length = 20)
    private String externalId;    // matches CSV’s product_id

    @Column(nullable = false)
    private String name;          // product_name

    private String category;      // product_category
    private String brand;         // brand
    private BigDecimal packageQuantity; // package_quantity
    private String packageUnit;   // package_unit
}
