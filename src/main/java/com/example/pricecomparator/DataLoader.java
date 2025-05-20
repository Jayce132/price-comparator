package com.example.pricecomparator;

import com.example.pricecomparator.service.CsvIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final CsvIngestionService csvService;

    public DataLoader(CsvIngestionService csvService) {
        this.csvService = csvService;
    }

    @Override
    public void run(String... args) throws Exception {
        csvService.ingestAll();
    }
}
