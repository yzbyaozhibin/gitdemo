package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    List<Cart> mergeCart(String userId, List<Cart> carts);

    List<Cart> findCartFromRedis(String userId);

    void addItemToCartByRedis(String userId, Long itemId, Integer num);

    void saveChoseCart(String userId, List<Cart> carts);

    List<Cart> findTempCart(String userId);
}
