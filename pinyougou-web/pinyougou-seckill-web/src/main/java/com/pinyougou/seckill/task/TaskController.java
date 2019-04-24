package com.pinyougou.seckill.task;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.PayService;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时任务控制器
 *
 * @version 1.0
 * @date 2019/4/24
 */
@Component
public class TaskController {

    @Reference(timeout = 10000)
    private PayService payService;

    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;

    @Scheduled(cron = "0/3 * * * * ?")
    public void CheckSeckillOrderStatus(){
        try {
            //从redis中查找所有过期的订单
            List<SeckillOrder> seckillOrders = seckillOrderService.findCloseOrder();
            for (SeckillOrder seckillOrder : seckillOrders) {
                Map<String, Object> resMap = payService.closeOrder(seckillOrder.getId().toString());
                if (resMap.get("resultCode").equals("1")) {
                    //如果订单关闭成功则删除redis缓存中的订单,并恢复订单相应商品的库存
                    seckillOrderService.closeOrderFromRedis(seckillOrder.getUserId(),seckillOrder.getSeckillId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
