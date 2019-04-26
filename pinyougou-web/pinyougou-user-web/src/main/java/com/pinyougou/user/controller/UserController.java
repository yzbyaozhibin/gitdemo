package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import com.pinyougou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController
 *
 * @version 1.0
 * @date 2019/4/15
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Reference(timeout = 10000)
    private UserService userService;
    @Reference(timeout = 10000)
    private ProvincesService provincesService;
    @Reference(timeout = 10000)
    private CitiesService citiesService;

    @PostMapping("/save")
    public Boolean save(@RequestBody User user,String code) {
        try {
            if (userService.checkSmsCode(code)) {
                userService.save(user);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/sendSms")
    public Boolean sendSms(String phone) {
        try {
            return userService.sendSms(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/showUser")
    public Map<String, Object> showUser() {
        Map<String, Object> map = new HashMap<>();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username", username);
        return map;
    }

    @GetMapping("/findProvinces")
    public List<Provinces> findProvinces(){
        return provincesService.findAll();
    }

    @GetMapping("/findCitiesByParentId")
    public List<Cities> findCitiesByParentId(@RequestParam String parentId){
        return citiesService.findCitiesByParentId(parentId);
    }


}
