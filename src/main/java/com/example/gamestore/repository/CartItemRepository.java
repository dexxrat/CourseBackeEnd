package com.example.gamestore.repository;

import com.example.gamestore.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.game.id = :gameId")
    Optional<CartItem> findByCartIdAndGameId(@Param("cartId") Long cartId, @Param("gameId") Long gameId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.game WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartIdWithGame(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.game.id = :gameId")
    void deleteByCartIdAndGameId(@Param("cartId") Long cartId, @Param("gameId") Long gameId);

    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart.id = :cartId")
    long countByCartId(@Param("cartId") Long cartId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.cart JOIN FETCH ci.game WHERE ci.id = :itemId")
    Optional<CartItem> findByIdWithAssociations(@Param("itemId") Long itemId);

    @Query("SELECT ci FROM CartItem ci JOIN ci.cart c JOIN c.user u WHERE u.id = :userId")
    List<CartItem> findByUserId(@Param("userId") Long userId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.game JOIN ci.cart c JOIN c.user u WHERE u.id = :userId")
    List<CartItem> findByUserIdWithGames(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.game.id = :gameId")
    boolean existsByCartIdAndGameId(@Param("cartId") Long cartId, @Param("gameId") Long gameId);

    @Modifying
    @Transactional
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :itemId AND ci.cart.id = :cartId")
    int updateQuantity(@Param("cartId") Long cartId, @Param("itemId") Long itemId, @Param("quantity") Integer quantity);


}