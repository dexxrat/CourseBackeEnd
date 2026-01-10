package com.example.gamestore.service;

import com.example.gamestore.model.Cart;
import com.example.gamestore.model.CartItem;
import com.example.gamestore.repository.CartItemRepository;
import com.example.gamestore.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    public Optional<CartItem> findById(Long id) {
        return cartItemRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> findByIdWithAssociations(Long id) {
        return cartItemRepository.findByIdWithAssociations(id);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByCartId(Long cartId) {
        return cartItemRepository.findByCartId(cartId);
    }

    @Transactional(readOnly = true)
    public List<CartItem> findByCartIdWithGame(Long cartId) {
        return cartItemRepository.findByCartIdWithGame(cartId);
    }

    @Transactional(readOnly = true)
    public Optional<CartItem> findByCartIdAndGameId(Long cartId, Long gameId) {
        return cartItemRepository.findByCartIdAndGameId(cartId, gameId);
    }

    @Transactional(readOnly = true)
    public boolean existsByCartIdAndGameId(Long cartId, Long gameId) {
        return cartItemRepository.existsByCartIdAndGameId(cartId, gameId);
    }

    @Transactional(readOnly = true)
    public long countByCartId(Long cartId) {
        return cartItemRepository.countByCartId(cartId);
    }

    @Transactional
    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    @Transactional
    public void delete(CartItem cartItem) {
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public void deleteById(Long id) {
        cartItemRepository.deleteById(id);
    }

    @Transactional
    public void deleteByCartId(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
    }

    @Transactional
    public void deleteByCartIdAndGameId(Long cartId, Long gameId) {
        cartItemRepository.deleteByCartIdAndGameId(cartId, gameId);
    }
}