package com.pinyougou.service;

public interface SmsService {

    Boolean sendSms(String phoneNum, String verify,
                    String signName, String templateCode);
}
