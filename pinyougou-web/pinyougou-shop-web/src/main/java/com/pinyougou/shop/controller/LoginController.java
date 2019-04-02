package com.pinyougou.shop.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @GetMapping("/getStatus")
    public Map<String,Object> getStatus() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Map<String, Object> map  = new HashMap<>();
        map.put("username",username);
        return map;
    }
}
