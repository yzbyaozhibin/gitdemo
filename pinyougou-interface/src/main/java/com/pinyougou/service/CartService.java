package com.pinyougou.service;

import com.pinyougou.cart.Cart;

import java.util.List;

public interface CartService {
    List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num);

    List<Cart> mergeCart(String username, List<Cart> carts);

    List<Cart> findCartFromRedis(String username);

    void addItemToCartByRedis(String username, Long itemId, Integer num);
}
