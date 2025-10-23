package com.goldloan.safegold1.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // item name (e.g., Ring, Necklace)
    private String category;    // category filter (e.g., Ring)
    private Double grams;       // weight in grams
    private Integer carats;     // purity (e.g., 22)
    private Double price;       // price in currency
    private String imageUrl;    // path or url to image
    @Column(length = 2000)
    private String description; // long description/specs
    private String tags;        // comma-separated tags for filtering
    private String gallery;     // comma-separated image URLs for carousel

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getGrams() { return grams; }
    public void setGrams(Double grams) { this.grams = grams; }

    public Integer getCarats() { return carats; }
    public void setCarats(Integer carats) { this.carats = carats; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getGallery() { return gallery; }
    public void setGallery(String gallery) { this.gallery = gallery; }
}