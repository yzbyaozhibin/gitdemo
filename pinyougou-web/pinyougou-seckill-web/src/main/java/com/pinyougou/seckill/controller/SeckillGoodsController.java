package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SeckillGoods;
import com.pinyougou.service.SeckillGoodsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 秒杀商品控制器
 *
 * @version 1.0
 * @date 2019/4/22
 */
@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Reference(timeout = 10000)
    private SeckillGoodsService seckillGoodsService;

    @GetMapping("/findSeckillGoods")
    public List<SeckillGoods> findSeckillGoods() {
        try {
            return seckillGoodsService.findSeckillGoodsFromRedis();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/showItems")
    public SeckillGoods findOne(Long id){
        try {
            return seckillGoodsService.findOneFromRedis(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
