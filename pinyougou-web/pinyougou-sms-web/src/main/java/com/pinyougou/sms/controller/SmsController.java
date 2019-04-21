package com.pinyougou.sms.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.SmsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * SmsController
 *
 * @version 1.0
 * @date 2019/4/15
 */
@RestController
@RequestMapping("/sms")
public class SmsController {

    @Reference(timeout = 10000)
    private SmsService smsService;

    @PostMapping("/sendSms")
    public Map<String, Object> sendSms(String phoneNum, String templateParam,
                                       String signName, String templateCode) {
        try {
            Boolean b = smsService.sendSms(phoneNum, templateParam, signName, templateCode);
            Map<String, Object> map = new HashMap<>();
            map.put("success",b);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
