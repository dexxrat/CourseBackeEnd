package com.example.gamestore.repository;

import com.example.gamestore.model.Cart;
import com.example.gamestore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Используем стандартный метод Spring Data JPA
    void deleteByCart(Cart cart);

    // Или можем удалить по ID корзины через стандартный метод
    void deleteByCartId(Long cartId);

    Optional<CartItem> findByCartIdAndGameId(Long cartId, Long gameId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.game WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithGame(@Param("cartId") Long cartId);

    List<CartItem> findByCartId(Long cartId);

    void deleteByCartIdAndGameId(Long cartId, Long gameId);

    long countByCartId(Long cartId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.cart JOIN FETCH ci.game WHERE ci.id = :itemId")
    Optional<CartItem> findByIdWithAssociations(@Param("itemId") Long itemId);

    boolean existsByCartIdAndGameId(Long cartId, Long gameId);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c JOIN c.user u WHERE u.id = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);
}