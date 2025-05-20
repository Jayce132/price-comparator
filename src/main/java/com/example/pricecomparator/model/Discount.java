package com.example.pricecomparator.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "discount",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"store_id","product_id","from_date","to_date"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(optional = false) @JoinColumn(name = "store_id")
    private Store store;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "percentage_of_discount", nullable = false)
    private Integer percentage;
}
