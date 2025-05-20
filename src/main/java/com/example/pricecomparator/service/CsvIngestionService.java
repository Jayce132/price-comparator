package com.example.pricecomparator.service;

import com.example.pricecomparator.model.*;
import com.example.pricecomparator.repository.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CsvIngestionService {
    private static final Logger log = LoggerFactory.getLogger(CsvIngestionService.class);

    private static final Pattern PRICE_FILE =
            Pattern.compile("(.+)_([0-9]{4}-[0-9]{2}-[0-9]{2})\\.csv");
    private static final Pattern DISC_FILE  =
            Pattern.compile("(.+)_discounts_([0-9]{4}-[0-9]{2}-[0-9]{2})\\.csv");

    private final ProductRepository      productRepo;
    private final StoreRepository        storeRepo;
    private final ProductPriceRepository priceRepo;
    private final DiscountRepository     discountRepo;

    public CsvIngestionService(ProductRepository productRepo,
                               StoreRepository storeRepo,
                               ProductPriceRepository priceRepo,
                               DiscountRepository discountRepo) {
        this.productRepo  = productRepo;
        this.storeRepo    = storeRepo;
        this.priceRepo    = priceRepo;
        this.discountRepo = discountRepo;
    }

    /**
     * Walk every subdirectory under src/main/resources/data,
     * ingest all price files first, then discount files.
     */
    public void ingestAll() {
        Path dataRoot = Paths.get("src","main","resources","data");
        if (!Files.isDirectory(dataRoot)) {
            log.warn("Data directory not found: {}", dataRoot.toAbsolutePath());
            return;
        }

        try (DirectoryStream<Path> stores = Files.newDirectoryStream(dataRoot)) {
            for (Path storeDir : stores) {
                if (!Files.isDirectory(storeDir)) continue;
                String storeName = storeDir.getFileName().toString();
                Store store = storeRepo.findByName(storeName)
                        .orElseGet(() -> storeRepo.save(Store.builder()
                                .name(storeName)
                                .build()));

                // 1) price files
                try (DirectoryStream<Path> files = Files.newDirectoryStream(storeDir, "*.csv")) {
                    for (Path csv : files) {
                        Matcher m = PRICE_FILE.matcher(csv.getFileName().toString());
                        if (m.matches() && m.group(1).equals(storeName)) {
                            LocalDate date = LocalDate.parse(m.group(2));
                            ingestPrices(csv, store, date);
                        }
                    }
                }

                // 2) discount files
                try (DirectoryStream<Path> files = Files.newDirectoryStream(storeDir, "*.csv")) {
                    for (Path csv : files) {
                        Matcher m = DISC_FILE.matcher(csv.getFileName().toString());
                        if (m.matches() && m.group(1).equals(storeName)) {
                            ingestDiscounts(csv, store);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Failed to scan data directory", ex);
        }
    }

    private void ingestPrices(Path csvPath, Store store, LocalDate snapshotDate) {
        log.info("Ingesting prices for store {} from {} on {}", store.getName(),
                csvPath.getFileName(), snapshotDate);
        CSVFormat fmt = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader("product_id","product_name","product_category","brand",
                        "package_quantity","package_unit","price","currency")
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, fmt)) {

            for (CSVRecord rec : parser) {
                try {
                    String pid = rec.get("product_id");
                    Product p = productRepo.findByExternalId(pid)
                            .orElseGet(() -> productRepo.save(Product.builder()
                                    .externalId(pid)
                                    .name(rec.get("product_name"))
                                    .category(rec.get("product_category"))
                                    .brand(rec.get("brand"))
                                    .packageQuantity(new BigDecimal(rec.get("package_quantity")))
                                    .packageUnit(rec.get("package_unit"))
                                    .build()));

                    priceRepo.save(ProductPrice.builder()
                            .product(p)
                            .store(store)
                            .price(new BigDecimal(rec.get("price")))
                            .currency(rec.get("currency"))
                            .date(snapshotDate)
                            .build());
                } catch (Exception rowEx) {
                    log.warn("Skipping bad price row {}: {}", rec.getRecordNumber(), rowEx.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Failed to ingest prices from " + csvPath, e);
        }
    }

    private void ingestDiscounts(Path csvPath, Store store) {
        log.info("Ingesting discounts for store {} from {}", store.getName(),
                csvPath.getFileName());
        CSVFormat fmt = CSVFormat.DEFAULT.builder()
                .setDelimiter(';')
                .setHeader("product_id","product_name","brand","package_quantity",
                        "package_unit","product_category","from_date","to_date",
                        "percentage_of_discount")
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .build();

        try (Reader reader = Files.newBufferedReader(csvPath, StandardCharsets.UTF_8);
             CSVParser parser = new CSVParser(reader, fmt)) {

            for (CSVRecord rec : parser) {
                try {
                    String pid = rec.get("product_id");
                    Product p = productRepo.findByExternalId(pid)
                            .orElseThrow(() -> new IllegalStateException("Missing product " + pid));

                    discountRepo.save(Discount.builder()
                            .product(p)
                            .store(store)
                            .fromDate(LocalDate.parse(rec.get("from_date")))
                            .toDate(LocalDate.parse(rec.get("to_date")))
                            .percentage(Integer.parseInt(rec.get("percentage_of_discount")))
                            .build());
                } catch (Exception rowEx) {
                    log.warn("Skipping bad discount row {}: {}", rec.getRecordNumber(), rowEx.getMessage());
                }
            }

        } catch (IOException e) {
            log.error("Failed to ingest discounts from " + csvPath, e);
        }
    }
}
