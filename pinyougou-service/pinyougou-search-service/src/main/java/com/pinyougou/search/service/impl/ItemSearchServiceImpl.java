package com.pinyougou.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.Item;
import com.pinyougou.service.ItemSearchService;
import com.pinyougou.solr.SolrItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * ItemSearchServiceImpl
 *
 * @version 1.0
 * @date 2019/4/9
 */
@Service(interfaceName = "com.pinyougou.service.ItemSearchService")
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map<String, Object> searchParams) {
        Map<String, Object> map = new HashMap<>();
        map.putAll(searchList(searchParams));

        List<String> categoryList = searchCategoryList(searchParams);
        map.put("categoryList", categoryList);
        if (categoryList != null && categoryList.size() > 0) {
            map.putAll(searchBrandAndSpecList(categoryList.get(0)));
        }
        return map;
    }

    @Override
    public void saveSolrItem(List<SolrItem> solrItemList) {
        UpdateResponse updateResponse = solrTemplate.saveBeans(solrItemList);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    @Override
    public void delSolrItem(Long[] ids) {
        SimpleQuery simpleQuery = new SimpleQuery();
        Criteria criteria = new Criteria("goodsId").in(ids);
        simpleQuery.addCriteria(criteria);
        UpdateResponse updateResponse = solrTemplate.delete(simpleQuery);
        if (updateResponse.getStatus() == 0) {
            solrTemplate.commit();
        } else {
            solrTemplate.rollback();
        }
    }

    private Map<String, Object> searchList(Map<String, Object> searchParams) {
        String keywords = (String) searchParams.get("keywords");
        Map<String, Object> resMap = new HashMap<>();
        List<SolrItem> content = null;
        Integer page = (Integer) searchParams.get("page");
        if (page == null || page < 1) {
            page = 1;
        }
        Integer rows = (Integer) searchParams.get("rows");
        if (rows == null || rows < 1) {
            rows = 10;
        }

        if (StringUtils.isNotBlank(keywords)) {
            HighlightQuery highLightQuery = new SimpleHighlightQuery();
            Criteria criteria = new Criteria("keywords").is(keywords);
            highLightQuery.addCriteria(criteria);
            //设置高亮参数
            HighlightOptions highlightOptions = new HighlightOptions();
            highlightOptions.addField("title");
            highlightOptions.setSimplePrefix("<font color='red'>");
            highlightOptions.setSimplePostfix("</font>");
            highLightQuery.setHighlightOptions(highlightOptions);

            //添加分类筛选条件
            String category = (String) searchParams.get("category");
            if (StringUtils.isNoneBlank(category)) {
                Criteria criteria1 = new Criteria("category").is(category);
                highLightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            //添加品牌筛选条件
            String brand = (String) searchParams.get("brand");
            if (StringUtils.isNoneBlank(brand)) {
                Criteria criteria1 = new Criteria("brand").is(brand);
                highLightQuery.addFilterQuery(new SimpleFilterQuery(criteria1));
            }
            //添加规格筛选条件
            Map<String, String> spec = (Map<String, String>) searchParams.get("spec");
            if (spec != null && spec.size() > 0) {
                Set<String> keySet = spec.keySet();
                for (String key : keySet) {
                    Criteria keyCriteria = new Criteria("spec_" + key).is(spec.get(key));
                    highLightQuery.addFilterQuery(new SimpleFilterQuery(keyCriteria));
                }
            }
            //添加价格的过滤条件
            String price = (String) searchParams.get("price");
            if (StringUtils.isNoneBlank(price)) {
                String[] priceArr = price.split("-");
                if (!priceArr[0].equals("0")) {
                    Criteria criteria1 = new Criteria("price").greaterThan(priceArr[0]);
                    highLightQuery.addFilterQuery(new SimpleFacetQuery(criteria1));
                }
                if (!priceArr[1].equals("*")) {
                    Criteria criteria1 = new Criteria("price").lessThan(priceArr[1]);
                    highLightQuery.addFilterQuery(new SimpleFacetQuery(criteria1));
                }
            }
            //排序
            String sortField = (String) searchParams.get("sortField");
            String sortValue = (String) searchParams.get("sortValue");
            if (StringUtils.isNoneBlank(sortField) && StringUtils.isNoneBlank(sortValue)) {
                Sort sort = null;
                if (sortValue.equals("ASC")) {
                    sort = new Sort(Sort.Direction.ASC, sortField);
                } else {
                    sort = new Sort(Sort.Direction.DESC, sortField);
                }
                highLightQuery.addSort(sort);
            }

            //分页查询
            highLightQuery.setOffset((page - 1) * rows);
            highLightQuery.setRows(rows);
            //封装了查询结果的对象
            HighlightPage<SolrItem> highlightPage = solrTemplate.queryForHighlightPage(highLightQuery, SolrItem.class);
            resMap.put("total", highlightPage.getTotalElements());
            resMap.put("totalPages", highlightPage.getTotalPages());

            List<HighlightEntry<SolrItem>> highlighted = highlightPage.getHighlighted();
            if (highlighted != null && highlighted.size() > 0) {
                for (HighlightEntry<SolrItem> entry : highlighted) {
                    SolrItem solrItem = entry.getEntity();
                    List<HighlightEntry.Highlight> highlights = entry.getHighlights();
                    if (highlights != null && highlights.size() > 0) {
                        List<String> snipplets = highlights.get(0).getSnipplets();
                        String s = snipplets.get(0);
                        solrItem.setTitle(s);
                    }

                }
            }
            content = highlightPage.getContent();
        } else {
            Query query = new SimpleQuery("*:*");
            query.setOffset((page - 1) * rows);
            query.setRows(rows);
            ScoredPage<SolrItem> scoredPage = solrTemplate.queryForPage(query, SolrItem.class);
            resMap.put("total", scoredPage.getTotalElements());
            resMap.put("totalPages", scoredPage.getTotalPages());
            content = scoredPage.getContent();
        }
        resMap.put("rows", content);
        return resMap;
    }

    private List<String> searchCategoryList(Map<String, Object> searchParams) {
        SimpleQuery simpleQuery = new SimpleQuery("*:*");

        //添加关键字查询条件
        String keywords = (String) searchParams.get("keywords");
        if (StringUtils.isNoneBlank(keywords)) {
            Criteria criteria = new Criteria("keywords").is(keywords);
            simpleQuery.addCriteria(criteria);
        }

        //设置分组
        GroupOptions categoryGroup = new GroupOptions().addGroupByField("category");
        simpleQuery.setGroupOptions(categoryGroup);
        GroupPage<SolrItem> solrItems = solrTemplate.queryForGroupPage(simpleQuery, SolrItem.class);
        Page<GroupEntry<SolrItem>> groupEntries = solrItems.getGroupResult("category").getGroupEntries();
        System.out.println(groupEntries);
        List<GroupEntry<SolrItem>> content = groupEntries.getContent();
        List<String> list = new ArrayList<>();
        for (GroupEntry<SolrItem> solrItemGroupEntry : content) {
            list.add(solrItemGroupEntry.getGroupValue());
        }
        return list;
    }

    private Map searchBrandAndSpecList(String categoryName) {
        Map map = new HashMap();
        Long typeId = (Long) redisTemplate.boundHashOps("categoryList").get(categoryName);
        List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
        List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("brandList", brandList);
        map.put("specList", specList);
        return map;
    }
}
