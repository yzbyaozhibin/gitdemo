package com.pinyougou.order.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * OrderServiceImpl
 *
 * @version 1.0
 * @date 2019/4/20
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void save(Order order) {
        try {
            //从内存中获取购物车
            List<Cart> cartList = (List<Cart>) redisTemplate.boundValueOps("cart_" + order.getUserId()).get();
            for (Cart cart : cartList) {
                Order order1 = new Order();
                order1.setOrderId(idWorker.nextId());
    //            order1.setPayment(new BigDecimal("0"));
                order1.setPaymentType(order.getPaymentType());
    //            order1.setPostFee("");
                order1.setStatus("1");
                order1.setCreateTime(new Date());
                order1.setUpdateTime(order1.getCreateTime());
    //            order1.setPaymentTime(new Date());
    //            order1.setConsignTime(new Date());
    //            order1.setEndTime(new Date());
    //            order1.setCloseTime(new Date());
    //            order1.setShippingName("");
    //            order1.setShippingCode("");
                order1.setUserId(order.getUserId());
                order1.setReceiverAreaName(order.getReceiverAreaName());
                order1.setReceiverMobile(order.getReceiverMobile());
                order1.setReceiver(order.getReceiver());
    //            order1.setExpire(new Date().);
    //            order1.setInvoiceType("");
                order1.setSourceType("2");
                order1.setSellerId(cart.getSellerId());
                double money = 0;
                for (OrderItem orderItem : cart.getOrderItems()) {
                    OrderItem orderItem1 = new OrderItem();
                    orderItem1.setId(idWorker.nextId());
                    orderItem1.setItemId(orderItem.getItemId());
                    orderItem1.setGoodsId(orderItem.getGoodsId());
                    orderItem1.setOrderId(order1.getOrderId());
                    orderItem1.setTitle(orderItem.getTitle());
                    orderItem1.setPrice(orderItem.getPrice());
                    orderItem1.setNum(orderItem.getNum());
                    orderItem1.setTotalFee(orderItem.getTotalFee());
                    orderItem1.setPicPath(orderItem.getPicPath());
                    orderItem1.setSellerId(cart.getSellerId());
                    money += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insertSelective(orderItem1);
                }
                order1.setPayment(new BigDecimal(money));
                orderMapper.insertSelective(order1);
            }
            redisTemplate.delete("cart_" + order.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Order order) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Order findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Order> findAll() {
        return null;
    }

    @Override
    public List<Order> findByPage(Order order, int page, int rows) {
        return null;
    }
}
