package com.example.gamestore.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 120)
    private String password;

    @Column(name = "active")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public Boolean isActive() {
        return active;
    }

    public void createCart() {
        if (this.cart == null) {
            Cart newCart = new Cart();
            newCart.setUser(this);
            this.cart = newCart;
        }
    }
}