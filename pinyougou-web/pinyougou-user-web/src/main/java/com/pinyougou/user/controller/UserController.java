package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.PayLog;
import com.pinyougou.pojo.Cities;
import com.pinyougou.pojo.Provinces;
import com.pinyougou.pojo.User;
import com.pinyougou.service.OrderService;
import com.pinyougou.service.PayLogService;
import com.pinyougou.service.PayService;
import com.pinyougou.service.CitiesService;
import com.pinyougou.service.ProvincesService;
import com.pinyougou.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Reference(timeout = 10000)
    private OrderService orderService;

    @Autowired
    private HttpServletRequest request;

    @Reference(timeout = 10000)
    private PayService payService;
    @Reference(timeout = 10000)
    private PayLogService payLogService;

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


    //获取用户订单信息
    @GetMapping("/getUserOrder")
    public PageResult getUserOrder(@RequestParam("page")int page,@RequestParam("rows") int rows ){

        try {
            //获取登录用户名
            String userId = request.getRemoteUser();
            return orderService.getUserOrder(page,rows,userId);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;
    }


    @GetMapping("/genPayCode")
    public Map<String, Object> genPayCode(String outTradeNo,String totalFee) {
        try {
            return payService.genPayCode(outTradeNo,totalFee);
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
    public Boolean updatePayLog(String outTradeNo, String transactionId) {
        try {
            String userId = request.getRemoteUser();
            payLogService.updatePayLog(userId,outTradeNo,transactionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @GetMapping("/findProvinces")
    public List<Provinces> findProvinces(){
        return provincesService.findAll();
    }

    @GetMapping("/findCitiesByParentId")
    public List<Cities> findCitiesByParentId(@RequestParam(value = "parentId") String parentId){
        return citiesService.findCitiesByParentId(parentId);
    }

    //修改密码
    @PostMapping("/changePW")
    public User changePW(@RequestBody Map<String, Object> userMap, HttpServletRequest request){
        String password = (String) userMap.get("password");
        String pass1 = DigestUtils.md5DigestAsHex(password.getBytes());
        String username = request.getRemoteUser();
        User user1=userService.selectUser(username);

        String oldPassword = user1.getPassword();
        if(oldPassword.equals(pass1)){
            userService.updatePassWord((String) userMap.get("newPassword"),username);
            user1.setPassword(null);
            return user1;
        }
        return null;
    }

    //根据用户名或取手机号码
    @GetMapping("showPhone")
    public String showPhone(HttpServletRequest request){
        String username = request.getRemoteUser();
        User user1 =userService.selectUser(username);
        String phone = user1.getPhone();
        return phone;
    }

    @GetMapping("/checkCode")
    public Boolean checkCode(String code){
       return userService.checkSmsCode(code);
    }
    @PostMapping("/updatePhone")
    public Boolean updatePhone(String picCode,String code,@RequestBody User user,HttpServletRequest request){
        try {
            String username = request.getRemoteUser();
            String verify_code = (String) request.getSession().getAttribute("verify_code");
            if(verify_code.equals(picCode)){
                if (userService.checkSmsCode(code)){
                    user.setUsername(username);
                    userService.update(user);
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
