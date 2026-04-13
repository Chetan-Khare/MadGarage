package com.madgarage.api.config;

import com.madgarage.api.enums.Role;
import com.madgarage.api.model.User;
import com.madgarage.api.repository.UserRepository;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Configuration
@ConditionalOnProperty(name = "app.seeder.enabled", havingValue = "true")
public class DatabaseSeeder {

    @Value("${app.seed.password}")
    private String seedPassword;

    /**
     * P0 FIX: Manual Flyway Repair.
     * This bypasses the version-specific "FlywayMigrationStrategy" package issues.
     * It uses the core Flyway library directly with the app's DataSource.
     */
    @Bean
    @Order(1)
    public CommandLineRunner runFlywayMigration(DataSource dataSource) {
        return args -> {
            System.out.println("🚀 [Manual Flyway] Repairing and Migrating...");
            Flyway flyway = Flyway.configure()
                    .dataSource(dataSource)
                    .baselineOnMigrate(true)
                    .load();
            flyway.repair();
            flyway.migrate();
            System.out.println("✅ Flyway Migration Successful.");
        };
    }

    @Bean
    @Order(2)
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            // 1. Sync Base Users
            syncUser(userRepository, passwordEncoder, "admin@madgarage.com", "Admin", "User", Role.ROLE_ADMIN);
            syncUser(userRepository, passwordEncoder, "seller@madgarage.com", "Premium", "Seller", Role.ROLE_SELLER);
            System.out.println("✅ Base users synchronized.");
        };
    }

    private void syncUser(UserRepository repo, PasswordEncoder encoder, String email, String fName, String lName, Role role) {
        User user = repo.findByEmail(email).orElse(null);
        if (user == null) {
            user = User.builder().email(email).firstName(fName).lastName(lName).isActive(true).build();
        }
        user.setPassword(encoder.encode(seedPassword));
        user.setRole(role);
        repo.save(user);
    }
}