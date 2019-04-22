package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.utils.IdWorker;
import com.pinyougou.pojo.Address;
import com.pinyougou.pojo.Order;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.service.AddressService;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.PayLogService;
import com.pinyougou.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * AddressController
 *
 * @version 1.0
 * @date 2019/4/20
 */
@RestController
@RequestMapping("/order")
public class AddressController {

    @Autowired
    HttpServletRequest request;

    @Reference(timeout = 10000)
    private AddressService addressService;

    @Reference(timeout = 10000)
    private OrderService orderService;

    @Reference(timeout = 10000)
    private PayService payService;

    @Reference(timeout = 10000)
    private PayLogService payLogService;


    @GetMapping("/getAddress")
    public List<Address> getAddress() {
        try {
            String userId = request.getRemoteUser();
            return addressService.findByUserId(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/saveOrder")
    public Boolean saveOrder(@RequestBody Order order) {
        try {
            String userId = request.getRemoteUser();
            order.setUserId(userId);
            orderService.save(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody Address address){
        try {
            String userId = request.getRemoteUser();
            address.setUserId(userId);
            addressService.save(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody Address address){
        try {
            String userId = request.getRemoteUser();
            address.setUserId(userId);
            addressService.update(address);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode() {
        try {
            String userId = request.getRemoteUser();
            PayLog payLog = payLogService.findPayLogFromRedis(userId);
            return payService.genPayCode(payLog.getOutTradeNo(), String.valueOf(payLog.getTotalFee()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/getStatus")
    public Map<String, Object> getStatus(String outTradeNo) {
        try {
            Map<String, Object> map = payService.getStatus(outTradeNo);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/updatePayLog")
    public Boolean updatePayLog(String transactionId) {
        try {
            String userId = request.getRemoteUser();
            payLogService.updatePayLog(userId,transactionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
