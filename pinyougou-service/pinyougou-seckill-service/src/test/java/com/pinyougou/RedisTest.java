package com.pinyougou;

import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.pojo.SeckillOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * 1
 *
 * @version 1.0
 * @date 2019/4/22
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext-service.xml")
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

//    @Test
//    public void t2(){
//        SeckillOrder seckillOrder = new SeckillOrder();
//        seckillOrder.setId(1120l);
//        redisTemplate.boundHashOps("test").put(seckillOrder.getId(),seckillOrder);
//    }

    @Test
    public void t1(){
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps("test2").get(149187842867958l);
        System.out.println(seckillGoods.getId());
    }

}
