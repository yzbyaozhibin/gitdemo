package com.pinyougou.item.listener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import java.io.File;

/**
 * DelPageMessageListener
 *
 * @version 1.0
 * @date 2019/4/13
 */

public class DelPageMessageListener implements SessionAwareMessageListener<ObjectMessage>{

    @Value("${staticUrl}")
    private String staticUrl;

    @Override
    public void onMessage(ObjectMessage objectMessage, Session session) throws JMSException {
        Long[] ids = (Long[]) objectMessage.getObject();
        for (Long goodsId : ids) {
           File file = new File(staticUrl + goodsId + ".html");
           if (file.exists()) {
               file.delete();
           }
        }
    }
}
