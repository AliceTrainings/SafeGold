package com.goldloan.safegold1.repository;

import com.goldloan.safegold1.model.Inquiry;
import com.goldloan.safegold1.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    List<Inquiry> findByProduct(Product product);
}




