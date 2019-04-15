package com.pinyougou.search.listener;
import java.math.BigDecimal;
import java.util.*;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.GoodsService;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.service.ItemService;
import com.pinyougou.solr.SolrItem;
import org.springframework.jms.listener.SessionAwareMessageListener;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * 添加索引库的消息监听器
 *
 * @version 1.0
 * @date 2019/4/13
 */
public class AddSolrMessageListener implements SessionAwareMessageListener<ObjectMessage> {

    @Reference(timeout = 10000)
    private GoodsService goodsService;

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(ObjectMessage om, Session session) throws JMSException {
        Long[] ids = (Long[]) om.getObject();
        List<Item> itemList = goodsService.findItemByIds(ids);
        List<SolrItem> solrItemList = new ArrayList<>();
        for (Item item : itemList) {
            SolrItem solrItem = new SolrItem();
            solrItem.setId(item.getId());
            solrItem.setTitle(item.getTitle());
            solrItem.setPrice(item.getPrice());
            solrItem.setImage(item.getImage());
            solrItem.setGoodsId(item.getGoodsId());
            solrItem.setCategory(item.getCategory());
            solrItem.setBrand(item.getBrand());
            solrItem.setSeller(item.getSeller());
            solrItem.setUpdateTime(item.getCreateTime());
            solrItem.setSpecMap(JSON.parseObject(item.getSpec(), Map.class));
            solrItemList.add(solrItem);
        }
        itemSearchService.saveSolrItem(solrItemList);
    }
}
