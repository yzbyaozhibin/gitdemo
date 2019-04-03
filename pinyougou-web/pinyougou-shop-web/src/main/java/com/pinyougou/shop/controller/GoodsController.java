package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GoodsController
 *
 * @version 1.0
 * @date 2019/4/3
 */

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @PostMapping("/save")
    public Boolean save(@RequestBody Goods goods) {
        try {
            goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
