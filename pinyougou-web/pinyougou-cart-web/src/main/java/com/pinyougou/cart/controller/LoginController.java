package com.pinyougou.cart.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * LoginController
 *
 * @version 1.0
 * @date 2019/4/18
 */
@RestController
public class LoginController {

    @GetMapping("/user/showName")
    public Map<String, String> showName(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        String username = request.getRemoteUser();
        map.put("username", username);
        return map;
    }

}
