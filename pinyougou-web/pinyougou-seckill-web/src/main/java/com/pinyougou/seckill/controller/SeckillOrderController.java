package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillOrder;
import com.pinyougou.service.PayService;
import com.pinyougou.service.SeckillOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 秒杀订单控制器
 *
 * @version 1.0
 * @date 2019/4/22
 */
@RestController
@RequestMapping("/order")
public class SeckillOrderController {
    @Reference(timeout = 10000)
    private SeckillOrderService seckillOrderService;

    @Reference(timeout = 10000)
    private PayService payService;

    @GetMapping("/getPayInfo")
    public Map<String, Object> getPayInfo(Long id, HttpServletRequest request) {
        try {
            String userId = request.getRemoteUser();
            //保存订单到redis
            SeckillOrder seckillOrder = seckillOrderService.saveToRedis(userId, id);
            Long money = seckillOrder.getMoney().multiply(new BigDecimal("100")).longValue();
            //获取二维码
            return payService.genPayCode(seckillOrder.getId().toString(),money.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/getStatus")
    public Map<String, Object> getStatus(String outTradeNo) {
        try {
            return payService.getStatus(outTradeNo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @GetMapping("/save")
    public Boolean save(String outTradeNo, String transactionId,HttpServletRequest request) {
        try {
            String userId = request.getRemoteUser();
            seckillOrderService.saveSeckillOrder(outTradeNo,transactionId,userId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
