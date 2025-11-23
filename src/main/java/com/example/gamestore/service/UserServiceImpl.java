package com.example.gamestore.service;

import com.example.gamestore.dto.UserDTO;
import com.example.gamestore.model.Cart;
import com.example.gamestore.model.Role;
import com.example.gamestore.model.User;
import com.example.gamestore.repository.CartRepository;
import com.example.gamestore.repository.RoleRepository;
import com.example.gamestore.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
                           CartRepository cartRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        User savedUser = userRepository.save(user);

        createCartIfNotExists(savedUser);

        return savedUser;
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User createUser(String username, String email, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setActive(true);

        Role userRole = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("User role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        createCartIfNotExists(savedUser);

        return savedUser;
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getUsername() != null && !userDetails.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDetails.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDetails.getUsername());
        }

        if (userDetails.getEmail() != null && !userDetails.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDetails.getEmail());
        }

        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        if (userDetails.getActive() != null) {
            user.setActive(userDetails.getActive());
        }

        User updatedUser = userRepository.save(user);

        createCartIfNotExists(updatedUser);

        return updatedUser;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> findByActiveTrue() {
        return userRepository.findAll().stream()
                .filter(user -> Boolean.TRUE.equals(user.isActive()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::convertToDTO);
    }

    @Override
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDTO);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDTO.getUsername() != null && !userDTO.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsername(userDTO.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            user.setUsername(userDTO.getUsername());
        }

        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(userDTO.getEmail())) {
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(userDTO.getEmail());
        }

        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }

        if (userDTO.getActive() != null) {
            user.setActive(userDTO.getActive());
        }

        if (userDTO.getRoles() != null && !userDTO.getRoles().isEmpty()) {
            Set<Role> roles = new HashSet<>();
            for (String roleName : userDTO.getRoles()) {
                Role role = roleRepository.findByName(convertToRoleName(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
                roles.add(role);
            }
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);

        createCartIfNotExists(updatedUser);

        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    private void createCartIfNotExists(User user) {
        try {
            boolean cartExists = cartRepository.findByUserId(user.getId()).isPresent();
            if (!cartExists) {
                Cart cart = new Cart();
                cart.setUser(user);
                cart.setTotalPrice(BigDecimal.ZERO);
                cartRepository.save(cart);
                System.out.println("Created cart for user: " + user.getUsername());
            }
        } catch (Exception e) {
            System.err.println("Error creating cart for user " + user.getUsername() + ": " + e.getMessage());
        }
    }

    private UserDTO convertToDTO(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getNameAsString())
                .collect(Collectors.toSet());

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleNames,
                user.getActive(),
                user.getCreatedAt()
        );
    }

    private Role.RoleName convertToRoleName(String roleName) {
        try {
            String normalizedRoleName = roleName.toUpperCase();
            if (!normalizedRoleName.startsWith("ROLE_")) {
                normalizedRoleName = "ROLE_" + normalizedRoleName;
            }
            return Role.RoleName.valueOf(normalizedRoleName);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role name: " + roleName);
        }
    }
}