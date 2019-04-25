package com.pinyougou.cart.service.impl;
import java.math.BigDecimal;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * CartServiceImpl
 *
 * @version 1.0
 * @date 2019/4/18
 */
@Service(interfaceName = "com.pinyougou.service.CartService")
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Cart> addItemToCart(List<Cart> carts, Long itemId, Integer num) {
        try {
            Item item = itemMapper.selectByPrimaryKey(itemId);
            //根据商家id查是否有商家的购物车
            Cart cart = searchCartBySellerId(carts,item.getSellerId());
            if (cart == null) {
                cart = new Cart();
                cart.setSellerId(item.getSellerId());
                cart.setSeller(item.getSeller());
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(itemToOrderItem(item, num));
                cart.setOrderItems(orderItems);
                carts.add(cart);
            } else {
                //根据商品id找商家购物车 是否有这个商品
                List<OrderItem> orderItems = cart.getOrderItems();
                OrderItem orderItem = searchOrderItemByItemId(orderItems,itemId);
                if (orderItem == null) {
                    //将item转成orderItem
                    orderItem = itemToOrderItem(item, num);
                    orderItems.add(orderItem);
                } else {
                    orderItem.setNum(orderItem.getNum() + num);
                    orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));
                    if (orderItem.getNum() + num <= 0) {
                        orderItems.remove(orderItem);
                    }
                    if (orderItems.size() <= 0) {
                        carts.remove(cart);
                    }
                }
            }
            return carts;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> mergeCart(String username, List<Cart> carts) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        if (carts != null && carts.size() > 0) {
            for (Cart cart : carts) {
                for (OrderItem orderItem : cart.getOrderItems()) {
                    cartList = addItemToCart(cartList, orderItem.getItemId(), orderItem.getNum());
                }
            }
        }
        redisTemplate.boundValueOps("cart_" + username).set(cartList);
        return cartList;
    }

    @Override
    public List<Cart> findCartFromRedis(String username) {


        System.out.println("uuuu");
        return (List<Cart>) redisTemplate.boundValueOps("cart_" + username).get();
    }

    @Override
    public void addItemToCartByRedis(String username, Long itemId, Integer num) {
        List<Cart> cartList = findCartFromRedis(username);
        if (cartList == null) {
            cartList = new ArrayList<>();
        }
        cartList = addItemToCart(cartList,itemId,num);
        redisTemplate.boundValueOps("cart_" + username).set(cartList);
    }

    private OrderItem searchOrderItemByItemId(List<OrderItem> orderItems, Long itemId) {
        if (orderItems != null && orderItems.size() > 0) {
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getItemId().equals(itemId)) {
                    return orderItem;
                }
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> carts, String sellerId) {
        if (carts != null && carts.size() > 0) {
            for (Cart cart : carts) {
                if (cart.getSellerId().equals(sellerId)) {
                    return cart;
                }
            }
        }
        return null;
    }

    private OrderItem itemToOrderItem(Item item, Integer num) {
        OrderItem orderItem  = new OrderItem();
        orderItem.setItemId(item.getId());
        orderItem.setGoodsId(item.getGoodsId());
        orderItem.setTitle(item.getTitle());
        orderItem.setPrice(new BigDecimal(item.getPrice().doubleValue()));
        orderItem.setNum(num);
        orderItem.setTotalFee(new BigDecimal((item.getPrice().doubleValue())*num));
        orderItem.setPicPath(item.getImage());
        orderItem.setSellerId(item.getSellerId());
        return orderItem;
    }


}
