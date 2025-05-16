package com.example.pricecomparator;

import com.example.pricecomparator.model.Product;
import com.example.pricecomparator.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final ProductRepository repo;

    public DataLoader(ProductRepository repo) {
        this.repo = repo;
    }

    @Override
    public void run(String... args) {
        Product p = new Product();
        p.setName("Test Product");
        p.setBrand("Acme");
        p.setCategory("Gadgets");
        repo.save(p);

        repo.findAll().forEach(System.out::println);
    }
}
