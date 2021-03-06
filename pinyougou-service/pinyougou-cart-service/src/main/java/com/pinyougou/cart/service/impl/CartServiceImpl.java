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
import java.util.concurrent.TimeUnit;

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
                    if (orderItem.getNum() <= 0) {
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
    public List<Cart> mergeCart(String userId, List<Cart> carts) {
        try {
            List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
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
            redisTemplate.boundHashOps("cartList").put(userId,cartList);
            return cartList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> findCartFromRedis(String userId) {
        try {
            return (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addItemToCartByRedis(String userId, Long itemId, Integer num) {
        try {
            List<Cart> cartList = findCartFromRedis(userId);
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            cartList = addItemToCart(cartList,itemId,num);
            redisTemplate.boundHashOps("cartList" ).put(userId,cartList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveChoseCart(String userId,List<Cart> carts) {
        try {
            //存储临时购物车
            redisTemplate.boundHashOps("tempCartList").put(userId, carts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Cart> findTempCart(String userId) {
        try {
            return (List<Cart>) redisTemplate.boundHashOps("tempCartList").get(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
        try {
            if (carts != null && carts.size() > 0) {
                for (Cart cart : carts) {
                    if (cart.getSellerId().equals(sellerId)) {
                        return cart;
                    }
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private OrderItem itemToOrderItem(Item item, Integer num) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
