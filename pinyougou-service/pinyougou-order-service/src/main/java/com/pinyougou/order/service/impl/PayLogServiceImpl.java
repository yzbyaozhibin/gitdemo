package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.OrderMapper;
import com.pinyougou.mapper.PayLogMapper;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.PayLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * PayLogService
 *
 * @version 1.0
 * @date 2019/4/21
 */
@Service(interfaceName = "com.pinyougou.service.PayLogService")
@Transactional
public class PayLogServiceImpl implements PayLogService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private PayLogMapper payLogMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Override
    public void save(PayLog payLog) {

    }

    @Override
    public void update(PayLog payLog) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public PayLog findOne(Serializable id) {
        return null;
    }

    @Override
    public List<PayLog> findAll() {
        return null;
    }

    @Override
    public List<PayLog> findByPage(PayLog payLog, int page, int rows) {
        return null;
    }

    @Override
    public PayLog findPayLogFromRedis(String userId) {
        try {
            return  (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void updatePayLog(String userId, String transactionId) {
        try {
            PayLog payLog = (PayLog) redisTemplate.boundValueOps("payLog_" + userId).get();
            payLog.setPayTime(new Date());
            payLog.setTradeState("1");
            payLog.setTransactionId(transactionId);
            payLogMapper.updateByPrimaryKeySelective(payLog);

            String[] orderIds = payLog.getOrderList().split(",");
            for (String orderId : orderIds) {
                Order order = new Order();
                order.setOrderId(Long.valueOf(orderId));
                order.setUpdateTime(new Date());
                order.setPaymentTime(order.getUpdateTime());
                order.setStatus("2");
                orderMapper.updateByPrimaryKeySelective(order);
            }

            redisTemplate.delete("payLog_" + payLog.getUserId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
