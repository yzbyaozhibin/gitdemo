package com.pinyougou.seckill.service.impl;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.mapper.SeckillGoodsMapper;
import com.pinyougou.mapper.SeckillOrderMapper;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * 秒杀订单表
 *
 * @version 1.0
 * @date 2019/4/22
 */
@Service(interfaceName = "com.pinyougou.service.SeckillOrderService")
@Transactional
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void save() {

    }

    @Override
    public void update(SeckillOrder seckillOrder) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SeckillOrder findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SeckillOrder> findAll() {
        return null;
    }

    @Override
    public List<SeckillOrder> findByPage(SeckillOrder seckillOrder, int page, int rows) {
        return null;
    }

    @Override
    public synchronized SeckillOrder saveToRedis(String userId, Long id) {
        try {
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(id);
            //根据秒杀商品信息封装秒杀订单信息
            SeckillOrder seckillOrder = new SeckillOrder();
            long outTradeNo = idWorker.nextId();
            System.out.println("存储的outTradeNo"+outTradeNo);
            seckillOrder.setId(outTradeNo);
            seckillOrder.setSeckillId(seckillGoods.getId());
            seckillOrder.setMoney(seckillGoods.getCostPrice());
            seckillOrder.setUserId(userId);
            seckillOrder.setSellerId(seckillGoods.getSellerId());
            seckillOrder.setCreateTime(new Date());
            seckillOrder.setStatus("0");
            //将订单存入redis
            redisTemplate.boundHashOps("seckillOrder").put(userId,seckillOrder);
            //秒杀商品的剩余库存-1
            Integer stockCount = seckillGoods.getStockCount() - 1;
            if (stockCount <= 0) {//如果库存为0 ,移除缓存中的商品数据
                redisTemplate.boundHashOps("seckillGoodsList").delete(id);
            } else {//库存-1
                seckillGoods.setStockCount(stockCount);
                redisTemplate.boundHashOps("seckillGoodsList").put(id,seckillGoods);
            }
            return seckillOrder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveSeckillOrder(String outTradeNo, String transactionId, String userId) {
        try {
            System.out.println("页面传过来的outTradeNo:"+outTradeNo);
            //支付成功后先从redis获取订单 注意hashKey的类型要与存储时候的类型一致
            SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder" ).get(userId);
            //封装数据
            System.out.println(seckillOrder);
            seckillOrder.setPayTime(new Date());
            seckillOrder.setStatus("1");
            seckillOrder.setTransactionId(transactionId);
            //存入数据库,解放redis
            seckillOrderMapper.insertSelective(seckillOrder);
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SeckillOrder> findCloseOrder() {
        try {
            List<SeckillOrder> seckillOrders = new ArrayList<>();
            List<SeckillOrder> seckillOrderList = redisTemplate.boundHashOps("seckillOrder").values();
            if (seckillOrderList != null && seckillOrderList.size() > 0) {
                for (SeckillOrder seckillOrder : seckillOrderList) {
                    if (new Date().getTime() - seckillOrder.getCreateTime().getTime() > 5*60*1000) {
                        seckillOrders.add(seckillOrder);
                    }
                }
            }
            return seckillOrders;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeOrderFromRedis(String userId, Long seckillId) {
        try {
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("seckillGoodsList").get(seckillId);
            if (seckillGoods!=null) {
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }else {
                seckillGoods = seckillGoodsMapper.selectByPrimaryKey(seckillId);
            }
            redisTemplate.boundHashOps("seckillGoodsList").put(seckillId,seckillGoods);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
