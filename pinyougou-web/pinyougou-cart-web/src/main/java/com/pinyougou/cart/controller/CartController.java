package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.Cart;
import com.pinyougou.common.utils.CookieUtils;
import com.pinyougou.service.CartService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * CartController
 *
 * @version 1.0
 * @date 2019/4/18
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Reference(timeout = 10000)
    private CartService cartService;

    @GetMapping("/addToCarts")
    @CrossOrigin(origins = {"http://item.pinyougou.com"},allowCredentials = "true")
    public Boolean addToCarts(Long itemId, Integer num) {
        try {
            String username = request.getRemoteUser();
            if (StringUtils.isNoneBlank(username)) {
                cartService.addItemToCartByRedis(username, itemId, num);
            } else {
                List<Cart> carts = findCart();
                carts = cartService.addItemToCart(carts, itemId, num);
                CookieUtils.setCookie(request, response, "cartList",
                        JSON.toJSONString(carts),60*60*24,true);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    @GetMapping("/findCart")
    public List<Cart> findCart() {
        try {
            String username = request.getRemoteUser();
            String cartList = CookieUtils.getCookieValue(request, "cartList", true);
            List<Cart> carts;
            if (StringUtils.isNoneBlank(username)) {//表示登录状态
                if (StringUtils.isNoneBlank(cartList)) {//cookie有值,跟redis合并
                    carts = cartService.mergeCart(username, JSON.parseArray(cartList, Cart.class));
                    //删除cookie
                    CookieUtils.deleteCookie(request,response,"cartList");
                } else {//cookie无值,直接从redis获取
                    carts = cartService.findCartFromRedis(username);
                }
            } else { //未登录
                //防止购物车为null,业务层不会报错
                if (StringUtils.isBlank(cartList)) {
                    cartList = "[]";
                }
                carts = JSON.parseArray(cartList, Cart.class);
            }
            return carts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
