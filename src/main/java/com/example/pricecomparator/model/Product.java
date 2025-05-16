package com.example.pricecomparator.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;
    private String category;

    public Product(Long id, String name, String brand, String category) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.category = category;
    }

    public Product() {
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getBrand() {
        return this.brand;
    }

    public String getCategory() {
        return this.category;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString() {
        return "Product(id=" + this.getId() + ", name=" + this.getName() + ", brand=" + this.getBrand() + ", category=" + this.getCategory() + ")";
    }
}
