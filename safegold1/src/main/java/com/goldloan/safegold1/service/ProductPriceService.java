package com.goldloan.safegold1.service;

import com.goldloan.safegold1.model.Product;
import com.goldloan.safegold1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

@Service
public class ProductPriceService {

    @Autowired
    private ProductRepository productRepository;

    private final String GOLD_API_KEY = "goldapi-5z18ld4gkwkcye86-io";
    private final String GOLD_API_URL = "https://www.goldapi.io/api/XAU/INR";
    private final double GRAM_CONVERSION = 31.1035; // 1 troy ounce = 31.1035 grams

    /**
     * Get current gold rate per gram in INR
     * @return current gold rate per gram
     */
    public double getCurrentGoldRatePerGram() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-access-token", GOLD_API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            @SuppressWarnings("rawtypes")
            ResponseEntity<Map> response = restTemplate.getForEntity(GOLD_API_URL, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> data = response.getBody();
                if (data != null) {
                    Double pricePerOunce = (Double) data.get("price");
                    if (pricePerOunce != null) {
                        // Convert from per ounce to per gram
                        return pricePerOunce / GRAM_CONVERSION;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error fetching gold rate: " + e.getMessage());
        }
        
        // Fallback rate if API fails (approximate current rate)
        return 6500.0; // Default rate per gram
    }

    /**
     * Calculate product price based on gold rate and weight
     * @param grams weight of the product
     * @param carats purity of gold (18K, 22K, 24K)
     * @return calculated price
     */
    public double calculateProductPrice(double grams, int carats) {
        double goldRatePerGram = getCurrentGoldRatePerGram();
        
        // Adjust rate based on purity
        double purityMultiplier = getPurityMultiplier(carats);
        double adjustedRate = goldRatePerGram * purityMultiplier;
        
        // Calculate price: weight * adjusted rate
        return grams * adjustedRate;
    }

    /**
     * Get purity multiplier based on carats
     * @param carats gold purity
     * @return multiplier for price calculation
     */
    private double getPurityMultiplier(int carats) {
        switch (carats) {
            case 18: return 0.75;  // 18K = 75% gold
            case 20: return 0.83;   // 20K = 83.33% gold
            case 22: return 0.92;  // 22K = 91.67% gold
            case 24: return 1.0;    // 24K = 100% gold
            default: return 0.92;  // Default to 22K
        }
    }

    /**
     * Update all product prices based on current gold rates
     */
    public void updateAllProductPrices() {
        List<Product> products = productRepository.findAll();
        double currentGoldRate = getCurrentGoldRatePerGram();
        
        System.out.println("Updating product prices with gold rate: ₹" + currentGoldRate + " per gram");
        
        for (Product product : products) {
            double newPrice = calculateProductPrice(product.getGrams(), product.getCarats());
            product.setPrice(newPrice);
            productRepository.save(product);
            
            System.out.println("Updated " + product.getName() + " (" + product.getGrams() + "g, " + product.getCarats() + "K): ₹" + String.format("%.2f", newPrice));
        }
    }

    /**
     * Update price for a specific product
     * @param productId ID of the product to update
     */
    public void updateProductPrice(Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        if (product != null) {
            double newPrice = calculateProductPrice(product.getGrams(), product.getCarats());
            product.setPrice(newPrice);
            productRepository.save(product);
            
            System.out.println("Updated " + product.getName() + " price to ₹" + String.format("%.2f", newPrice));
        }
    }

    /**
     * Get current gold rate for display purposes
     * @return formatted gold rate string
     */
    public String getCurrentGoldRateDisplay() {
        double rate = getCurrentGoldRatePerGram();
        return String.format("₹%.2f/g", rate);
    }
}


