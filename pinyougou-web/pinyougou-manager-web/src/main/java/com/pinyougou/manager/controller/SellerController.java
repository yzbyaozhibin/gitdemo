package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Seller;
import com.pinyougou.service.SellerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Seller seller, Integer page, Integer rows) {
        try {
            if (StringUtils.isNoneBlank(seller.getName())) {
                seller.setName(new String(seller.getName().getBytes("ISO8859-1"),"utf-8"));
            }
            if (StringUtils.isNoneBlank(seller.getNickName())) {
                seller.setNickName(new String(seller.getNickName().getBytes("ISO8859-1"),"utf-8"));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sellerService.findByPage(seller, page, rows);
    }

    @PostMapping("/updateStatus")
    public Boolean updateStatus(@RequestBody Seller seller) {
        try {
            sellerService.updateStatus(seller);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
