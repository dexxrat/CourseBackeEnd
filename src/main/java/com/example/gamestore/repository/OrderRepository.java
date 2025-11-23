package com.example.gamestore.repository;

import com.example.gamestore.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByStatus(Order.OrderStatus status);
}