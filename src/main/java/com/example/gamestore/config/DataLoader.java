package com.example.gamestore.config;

import com.example.gamestore.model.Cart;
import com.example.gamestore.model.Role;
import com.example.gamestore.model.User;
import com.example.gamestore.repository.CartRepository;
import com.example.gamestore.repository.RoleRepository;
import com.example.gamestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Starting data initialization...");

        if (roleRepository.count() == 0) {
            Role adminRole = new Role();
            adminRole.setName(Role.RoleName.ROLE_ADMIN);
            roleRepository.save(adminRole);

            Role userRole = new Role();
            userRole.setName(Role.RoleName.ROLE_USER);
            roleRepository.save(userRole);

            System.out.println("Roles created successfully");
        }

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);

            Role adminRole = roleRepository.findByName(Role.RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Admin role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            User savedAdmin = userRepository.save(admin);

            Cart adminCart = new Cart();
            adminCart.setUser(savedAdmin);
            adminCart.setTotalPrice(java.math.BigDecimal.ZERO);
            cartRepository.save(adminCart);

            System.out.println("Admin user created successfully with cart");
        }

        if (userRepository.findByUsername("user").isEmpty()) {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setActive(true);

            Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("User role not found"));
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            user.setRoles(roles);

            User savedUser = userRepository.save(user);

            Cart userCart = new Cart();
            userCart.setUser(savedUser);
            userCart.setTotalPrice(java.math.BigDecimal.ZERO);
            cartRepository.save(userCart);

            System.out.println("Test user created successfully with cart");
        }

        createCartsForExistingUsers();

        System.out.println("Data initialization completed successfully");
    }

    private void createCartsForExistingUsers() {
        List<User> allUsers = userRepository.findAll();
        int createdCarts = 0;

        for (User user : allUsers) {
            boolean cartExists = cartRepository.findByUserId(user.getId()).isPresent();
            if (!cartExists) {
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setTotalPrice(java.math.BigDecimal.ZERO);
                cartRepository.save(cart);
                createdCarts++;
                System.out.println("Created cart for user: " + user.getUsername());
            }
        }

        if (createdCarts > 0) {
            System.out.println("Created " + createdCarts + " missing carts for existing users");
        }
    }
}