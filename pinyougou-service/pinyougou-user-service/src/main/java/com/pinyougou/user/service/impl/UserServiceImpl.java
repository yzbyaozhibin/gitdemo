package com.pinyougou.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pinyougou.common.utils.HttpClientUtils;
import com.pinyougou.mapper.UserMapper;
import com.pinyougou.pojo.User;
import com.pinyougou.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * UserServiceImpl
 *
 * @version 1.0
 * @date 2019/4/15
 */
@Service(interfaceName = "com.pinyougou.service.UserService")
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${signName}")
    private String signName;

    @Value("${templateCode}")
    private String templateCode;

    @Value("${smsUrl}")
    private String smsUrl;

    @Override
    public void save(User user) {
        user.setCreated(new Date());
        user.setUpdated(user.getCreated());
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));
        userMapper.insertSelective(user);
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public User findOne(Serializable id) {
        return null;
    }

    @Override
    public List<User> findAll() {
        return null;
    }

    @Override
    public List<User> findByPage(User user, int page, int rows) {
        return null;
    }

    @Override
    public Boolean sendSms(String phone) {
        try {
            String verify = UUID.randomUUID().toString().
                    replaceAll("-", "").
                    replaceAll("[a-zA-Z]", "").substring(0, 6);
            Map<String, String> paramsMap = new HashMap<>();
            paramsMap.put("phoneNum", phone);
            paramsMap.put("templateParam", "{'number':'"+ verify +"'}");
            paramsMap.put("signName", signName);
            paramsMap.put("templateCode", templateCode);
            HttpClientUtils httpClientUtils = new HttpClientUtils(false);
            String s = httpClientUtils.sendPost(smsUrl, paramsMap);
            Map map = JSON.parseObject(s, Map.class);
            Boolean b = (Boolean) map.get("success");
            if (b) {
                redisTemplate.boundValueOps("code").set(verify,90L,TimeUnit.SECONDS);
            }
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean checkSmsCode(String code) {
        String code1 = (String) redisTemplate.boundValueOps("code").get();
        return StringUtils.isNotBlank(code1)&&code1.equals(code);
    }
}
