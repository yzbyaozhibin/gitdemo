package com.pinyougou.item.listener;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.GoodsService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.*;
import java.util.Map;

/**
 * 上架添加详情页的监听器
 *
 * @version 1.0
 * @date 2019/4/13
 */

public class AddPageMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Value("${staticUrl}")
    private String staticUrl;

    @Override
    public void onMessage(ObjectMessage om, Session session) throws JMSException {
        Long[] ids = (Long[]) om.getObject();
        Configuration configuration = freeMarkerConfigurer.getConfiguration();
        Template template = null;
        try {
            template = configuration.getTemplate("item.ftl");
            for (Long id : ids) {
                Map<String, Object> dataModel = goodsService.getGoodsByGoodsId(id);
                OutputStreamWriter outputStreamWriter =
                        new OutputStreamWriter(new FileOutputStream(staticUrl+id+".html"),"UTF-8");
                template.process(dataModel, outputStreamWriter);
                outputStreamWriter.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
