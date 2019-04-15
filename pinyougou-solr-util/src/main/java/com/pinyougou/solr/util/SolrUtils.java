package com.pinyougou.solr.util;

import com.alibaba.fastjson.JSON;
import com.pinyougou.mapper.ItemMapper;
import com.pinyougou.pojo.Item;
import com.pinyougou.solr.pojo.SolrItem;
import com.sun.tools.javac.jvm.Items;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * SolrUtils
 *
 * @version 1.0
 * @date 2019/4/9
 */
@Component
public class SolrUtils {

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    public void importItemData() {
        Item item1 = new Item();
        item1.setStatus("1");
        List<Item> items = itemMapper.select(item1);
        List<SolrItem> solrItemList = new ArrayList<>();
        for (Item item : items) {
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
            solrItem.setSpec(JSON.parseObject(item.getSpec(), Map.class));
            solrItemList.add(solrItem);
        }
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItemList);
        if(updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    public void deleteAll() {
        SimpleQuery simpleQuery = new SimpleQuery("*:*");
        UpdateResponse u = solrTemplate.delete(simpleQuery);
        if (u.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    public static void main(String[] args) {
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext-solr.xml");
        SolrUtils solrUtils = (SolrUtils) ac.getBean("solrUtils");
//        solrUtils.importItemData();
        solrUtils.deleteAll();
    }

}
