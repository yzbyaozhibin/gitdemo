package com.pinyougou.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pinyougou.common.utils.HttpClientUtils;
import com.pinyougou.service.PayService;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * PayServiceImpl
 *
 * @version 1.0
 * @date 2019/4/21
 */
@Service(interfaceName = "com.pinyougou.service.PayService")
public class PayServiceImpl implements PayService {

    @Value("${appid}")
    private String appid;

    @Value("${partner}")
    private String partner;

    @Value("${partnerkey}")
    private String partnerkey;

    @Value("${unifiedorder}")
    private String unifiedorder;

    @Value("${orderQuery}")
    private String orderQuery;

    @Override
    public Map<String, Object> genPayCode(String outTradeNo, String totalFee) {

        try {
            Map<String,String> params = new HashMap<>();
            //封装请求参数
            params.put("appid",appid);
            params.put("mch_id",partner);
            params.put("nonce_str",WXPayUtil.generateNonceStr());
            params.put("body","品优购");
            params.put("out_trade_no",outTradeNo);
            params.put("total_fee", totalFee+"");
            params.put("spbill_create_ip","127.0.0.1");
            params.put("notify_url","http://test.pinyougou.com");
            params.put("trade_type","NATIVE");

            //发送请求
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String content = httpClientUtils.sendPost(unifiedorder, WXPayUtil.generateSignedXml(params, partnerkey));
            Map<String, String> resMap = WXPayUtil.xmlToMap(content);

            Map<String, Object> map = new HashMap<>();
            map.put("totalFee", totalFee);
            map.put("outTradeNo", outTradeNo);
            map.put("codeUrl", resMap.get("code_url"));
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> getStatus(String outTradeNo) {
        try {
            Map<String,String> params = new HashMap<>();
            //封装请求参数
            params.put("appid",appid);
            params.put("mch_id",partner);
            params.put("nonce_str",WXPayUtil.generateNonceStr());
            params.put("out_trade_no",outTradeNo);

            //发送请求
            HttpClientUtils httpClientUtils = new HttpClientUtils(true);
            String content = httpClientUtils.sendPost(orderQuery, WXPayUtil.generateSignedXml(params, partnerkey));
            Map<String, String> resMap = WXPayUtil.xmlToMap(content);
            Map<String, Object> map = new HashMap<>();
            map.put("status", resMap.get("trade_state"));
            map.put("transactionId",resMap.get("transaction_id"));
            return map;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
