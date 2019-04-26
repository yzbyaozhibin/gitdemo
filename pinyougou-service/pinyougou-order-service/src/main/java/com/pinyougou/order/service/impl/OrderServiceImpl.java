package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.OrderItemMapper;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.OrderItem;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;

/**
 * OrderServiceImpl
 *
 * @version 1.0
 * @date 2019/4/20
 */
@Service(interfaceName = "com.pinyougou.service.OrderService")
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public Map<String, String> save(Order order, List<Cart> tempCartList) {
        try {
//            //从内存中获取购物车
//            List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
            double totalFee = 0;
            String orderList = "";
            for (Cart cart : tempCartList) {
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
                orderList += order1.getOrderId() + ",";
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
                    //
                    totalFee += orderItem.getTotalFee().doubleValue();
                    //
                    orderItemMapper.insertSelective(orderItem1);
                }
                order1.setPayment(new BigDecimal(money));
                orderMapper.insertSelective(order1);
            }

            //保存支付日志
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));
            payLog.setCreateTime(new Date());
            payLog.setTotalFee((long) (totalFee * 100));
            payLog.setUserId(order.getUserId());
            payLog.setTradeState("0");
            payLog.setOrderList(orderList.substring(0, orderList.length() - 1));
            payLog.setPayType("1");
            //
            List<PayLog> payLogList = (List<PayLog>) redisTemplate.boundHashOps("payLog").get(order.getUserId());
            if (payLogList == null) {
                payLogList = new ArrayList<>();
                payLogList.add(payLog);
            }
            payLogList.add(payLog);
            redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLogList);
            //
            payLogMapper.insertSelective(payLog);
            String userId = order.getUserId();
            //从购物车中移除下单后的数据
            updateCartListByUserId(tempCartList, userId);

            Map<String, String> map = new HashMap<>();
            map.put("outTradeNo", payLog.getOutTradeNo());
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateCartListByUserId(List<Cart> tempCartList, String userId) {
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(userId);
        if (cartList != null && cartList.size() > 0) {
            for (Cart cart : cartList) {//单个商家对应的一个购物车
                for (Cart cart1 : tempCartList) {
                    if (cart.getSellerId().equals(cart1.getSellerId())) {
                        if (cart.getOrderItems() != null && cart.getOrderItems().size() > 0) {
                            //遍历单个商家的购物车,对相应的商品数量-1,
                            //////////////////
                            for (OrderItem orderItem : cart.getOrderItems()) {
                                for (OrderItem orderItem1 : cart1.getOrderItems()) {
                                    if (orderItem.getItemId().equals(orderItem1.getItemId())) {
                                        Integer num = orderItem.getNum() - orderItem1.getNum();
                                        if (num <= 0) {
                                            cart.getOrderItems().remove(orderItem);
                                        } else {
                                            orderItem.setNum(num);
                                        }
                                    }
                                }
                            }
                            //////////////////
                        }
                    }
                }
                if (cart.getOrderItems().size() <= 0) {
                    cartList.remove(cart);
                }
            }
            System.out.println(cartList);
        }
        if (cartList.size() <= 0) {
            redisTemplate.boundHashOps("cartList").delete(userId);
        } else {
            redisTemplate.boundHashOps("cartList").put(userId, cartList);
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

    @Override
    public PageResult getUserOrder(int page, int rows, String userId) {
        try {
            PageInfo<PayLog> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    //2. 根据用户名获取订单信息
                    //2.1 创建示范对象
                    Example example = new Example(PayLog.class);
                    //2.2 创建条件对象
                    Example.Criteria criteria = example.createCriteria();
                    //2.3 添加条件
                    criteria.andEqualTo("userId", userId);
                    example.orderBy("createTime").desc();
                    List<PayLog> payLogs = payLogMapper.selectByExample(example);
                }
            });
            List<PayLog> payLogs = pageInfo.getList();
            //1. 创建一个数组封装数据
            List<Map<String, Object>> payLogList = new ArrayList<>();
            if (payLogs != null && payLogs.size() > 0) {
                //遍历获取订单详情
                for (PayLog payLog : payLogs) {
                    //创建一个Map集合封装订单信息
                    Map<String, Object> payLogMap = new HashMap<>();
                    payLogMap.put("payLog", payLog);
                    String[] orderIds = payLog.getOrderList().split(",");
                    for (String orderId : orderIds) {
                        Order order1 = new Order();
                        order1.setOrderId(Long.valueOf(orderId));
                        Order order = orderMapper.selectOne(order1);
                        if (order!=null){
                            payLogMap.put("order", order);
                        }
                        //查询订单详情信息
                        Example example1 = new Example(OrderItem.class);
                        Example.Criteria criteria1 = example1.createCriteria();
                        criteria1.andEqualTo("orderId", order.getOrderId());
                        List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);
                        payLogMap.put("orderItem", orderItems);
                    }
                    payLogList.add(payLogMap);
                }
            }
            return new PageResult(pageInfo.getTotal(), payLogList);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
}
