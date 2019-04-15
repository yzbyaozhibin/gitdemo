package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Goods;
import com.pinyougou.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.jms.*;

/**
 * GoodsController
 *
 * @version 1.0
 * @date 2019/4/3
 */

@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private Destination addSolrQueue;

    @Autowired
    private Destination delSolrQueue;

    @Autowired
    private Destination addPageTopic;

    @Autowired
    private Destination delPageTopic;

    @PostMapping("/save")
    public Boolean save(@RequestBody Goods goods) {
        try {
            goods.setSellerId(SecurityContextHolder.getContext().getAuthentication().getName());
            goodsService.save(goods);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/findByPage")
    public PageResult findByPage(Goods goods, Integer page, Integer rows) {
        try {
            if (StringUtils.isNoneBlank(goods.getGoodsName())) {
                goods.setGoodsName(new String(goods.getGoodsName().getBytes("ISO8859-1"),"UTF-8"));
            }
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            goods.setSellerId(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return goodsService.findByPage(goods, page, rows);
    }

    @GetMapping("/updateIsMarketable")
    public Boolean updateIsMarketable(Long[] ids, String isMarketable) {
        try {
            goodsService.updateIsMarketable(ids, isMarketable);
            updateSolrItem(ids,isMarketable);
            updatePageItem(ids,isMarketable);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateSolrItem(Long[] ids, String isMarketable) {
        if (isMarketable.equals("1")) {
            jmsTemplate.send(addSolrQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage om = session.createObjectMessage();
                    om.setObject(ids);
                    return om;
                }
            });
        } else {
            jmsTemplate.send(delSolrQueue, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage om = session.createObjectMessage();
                    om.setObject(ids);
                    return om;
                }
            });
        }
    }

    private void updatePageItem(Long[] ids, String isMarketable) {
        if (isMarketable.equals("1")) {
            jmsTemplate.send(addPageTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage om = session.createObjectMessage();
                    om.setObject(ids);
                    return om;
                }
            });
        } else {
            jmsTemplate.send(delPageTopic, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    ObjectMessage om = session.createObjectMessage();
                    om.setObject(ids);
                    return om;
                }
            });
        }
    }


}
