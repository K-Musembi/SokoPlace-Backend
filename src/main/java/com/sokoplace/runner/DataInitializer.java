package com.sokoplace.runner;

import com.sokoplace.product.Product;
import com.sokoplace.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Populates the database with initial data
 * @see com.sokoplace.product.Product
 * @see com.sokoplace.product.ProductRepository
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        if (productRepository.count() == 0) {
            Product product1 = new Product(null, "lp001", "laptop", "lenovo", "ThinkBook 14 Gen 7", 100000.00,
                    "good laptop","/resources/images/laptop/lenovo/ThinkBook 14 Gen 7.jpg", new ArrayList<>(), null, null);
            Product product2 = new Product(null, "lp002", "laptop", "lenovo", "ThinkPad E14 Gen 6", 150000.00,
                    "good laptop","/resources/images/laptop/lenovo/ThinkPad E14 Gen 6.jpg", new ArrayList<>(), null, null);
            Product product3 = new Product(null, "lp003", "laptop", "lenovo", "Yoga 7 16IML9", 175000.00,
                    "good laptop","/resources/images/laptop/lenovo/Yoga 7 16IML9.jpg", new ArrayList<>(), null, null);
            Product product4 = new Product(null, "lp004", "laptop", "dell", "Inspiron 15 5000 Series (5570)", 105000.00,
                    "good laptop","/resources/images/laptop/dell/Inspiron 15 5000 Series (5570).jpg", new ArrayList<>(), null, null);
            Product product5 = new Product(null, "lp005", "laptop", "dell", "Alienware 17", 260000.00,
                    "good laptop","/resources/images/laptop/dell/Alienware 17.jpg", new ArrayList<>(), null, null);
            Product product6 = new Product(null, "lp006", "laptop", "dell", "Latitude 14 7000 Series", 130000.00
                    ,"good laptop","/resources/images/laptop/dell/Latitude 14 7000 Series.jpg", new ArrayList<>(), null, null);
            Product product7 = new Product(null, "lp007", "laptop", "asus", "ExpertBook B1", 120000.00,
                    "good laptop","/resources/images/laptop/asus/ExpertBook B1.jpg", new ArrayList<>(), null, null);
            Product product8 = new Product(null, "lp008", "laptop", "asus", "ExpertBook B3 Flip", 180000.00,
                    "good laptop","/resources/images/laptop/asus/ExpertBook B3 Flip.jpg", new ArrayList<>(), null, null);
            Product product9 = new Product(null, "lp009", "laptop", "asus", "ROG Flow Z13", 250000.00,
                    "good laptop","/resources/images/laptop/asus/ROG Flow Z13.jpg", new ArrayList<>(), null, null);
            Product product10 = new Product(null, "ph001", "phone", "samsung", "Galaxy S25", 125000.00,
                    "good phone", "/resources/images/phone/samsung/Galaxy S25.jpg", new ArrayList<>(), null, null);
            Product product11 = new Product(null, "ph002", "phone", "samsung", "Galaxy S25 Plus", 155000.00,
                    "good phone", "/resources/images/phone/samsung/Galaxy S25 Plus.jpg", new ArrayList<>(), null, null);
            Product product12 = new Product(null, "ph003", "phone", "samsung", "Galaxy Z Flip 5", 79999.00,
                    "good phone", "/resources/images/phone/samsung/Galaxy Z Flip 5.jpg", new ArrayList<>(), null, null);
            Product product13 = new Product(null, "ph004", "phone", "infinix", "Zero Flip", 90000.00,
                    "good phone", "/resources/images/phone/infinix/Zero Flip.jpg", new ArrayList<>(), null, null);
            Product product14 = new Product(null, "ph005", "phone", "infinix", "Zero 30 5G", 55000.00,
                    "good phone", "/resources/images/phone/infinix/Zero 30 5G.jpg", new ArrayList<>(), null, null);
            Product product15 = new Product(null, "ph006", "phone", "infinix", "Note 12 VIP", 45000.00,
                    "good phone", "/resources/images/phone/infinix/Note 12 VIP.jpg", new ArrayList<>(), null, null);
            Product product16 = new Product(null, "ph007", "phone", "oppo", "A3x 4G", 14700.00,
                    "good phone", "/resources/images/phone/oppo/A3x 4G.jpg", new ArrayList<>(), null, null);
            Product product17 = new Product(null, "ph008", "phone", "oppo", "Reno 10 5G", 60000.00,
                    "good phone", "/resources/images/phone/oppo/Reno 10 5G.jpg", new ArrayList<>(), null, null);
            Product product18 = new Product(null, "ph009", "phone", "oppo", "Find X5 Pro", 130000.00,
                    "good phone", "/resources/images/phone/oppo/Find X5 Pro.jpg", new ArrayList<>(), null, null);
            Product product19 = new Product(null, "pr001", "printer", "epson", "EcoTank L3150", 26999.00,
                    "good printer", "/resources/images/printer/epson/EcoTank L3150.jpg", new ArrayList<>(), null, null);
            Product product20 = new Product(null, "pr002", "printer", "epson", "EcoTank L4260", 59000.00,
                    "good printer", "/resources/images/printer/epson/EcoTank L4260.jpg", new ArrayList<>(), null, null);
            Product product21 = new Product(null, "pr003", "printer", "epson", "L1300 A3", 65000.00,
                    "good printer", "/resources/images/printer/epson/L1300 A3.jpg", new ArrayList<>(), null, null);
            Product product22 = new Product(null, "pr004", "printer", "hp", "M141a LaserJet", 30500.00,
                    "good printer", "/resources/images/printer/hp/M141a LaserJet.jpg", new ArrayList<>(), null, null);
            Product product23 = new Product(null, "pr005", "printer", "hp", "LaserJet Pro MFP", 70000.00,
                    "good printer", "/resources/images/printer/hp/LaserJet Pro MFP.jpg", new ArrayList<>(), null, null);
            Product product24 = new Product(null, "pr006", "printer", "hp", "Color LaserJet Pro MFP", 95000.00,
                    "good printer", "/resources/images/printer/hp/Color LaserJet Pro MFP.jpg", new ArrayList<>(), null, null);
            Product product25 = new Product(null, "pr007", "printer", "kyocera", "ECOSYS FS-1025 MFP", 40000.00,
                    "good printer", "/resources/images/printer/kyocera/ECOSYS FS-1025 MFP.jpg", new ArrayList<>(), null, null);
            Product product26 = new Product(null, "pr008", "printer", "kyocera", "ECOSYS MA3500cix", 90000.00,
                    "good printer", "/resources/images/kyocera/ECOSYS MA3500cix.jpg", new ArrayList<>(), null, null);
            Product product27 = new Product(null, "pr009", "printer", "kyocera", "ECOSYS P2235dn", 50000.00,
                    "good printer", "/resources/images/printer/kyocera/ECOSYS P2235dn.jpg", new ArrayList<>(), null, null);

            productRepository.saveAll(List.of(product1, product2, product3, product4, product5, product6, product7, product8, product9,
                    product10, product11, product12, product13, product14, product15, product16, product17, product18, product19,
                    product20, product21, product22, product23, product24, product25, product26, product27));
        }
    }
}
