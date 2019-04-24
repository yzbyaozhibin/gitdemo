package com.pinyougou.seckill.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取登录用户名控制器
 *
 * @version 1.0
 * @date 2019/4/22
 */
@RestController
public class LoginController {

    @GetMapping("/login/showName")
    public Map<String, String> showName(HttpServletRequest request) {
        String username = request.getRemoteUser();
        Map<String, String> map =  new HashMap<>();
        map.put("username", username);
        return map;
    }
}
