package com.pinyougou.service;

import java.util.Map;

public interface PayService {
    Map<String,Object> genPayCode(String outTradeNo, String totalFee);

    Map<String,Object> getStatus(String outTradeNo);
}
