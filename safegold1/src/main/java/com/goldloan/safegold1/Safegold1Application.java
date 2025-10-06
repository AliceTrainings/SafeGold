package com.goldloan.safegold1;

import com.goldloan.safegold1.model.User;
import com.goldloan.safegold1.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Safegold1Application {

	public static void main(String[] args) {
		SpringApplication.run(Safegold1Application.class, args);
	}

    @Bean
    public CommandLineRunner seedAdminUser(UserRepository userRepository) {
        return args -> {
            String adminEmail = "admin@safegold.local";
            userRepository.findByEmail(adminEmail).orElseGet(() -> {
                User admin = new User();
                admin.setName("Administrator");
                admin.setEmail(adminEmail);
                admin.setMobileNumber("0000000000");
                admin.setPassword("admin123"); // For demo only; replace in production
                admin.setRole("ADMIN");
                return userRepository.save(admin);
            });
        };
    }
}
