package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.*;
import com.pinyougou.pojo.*;
import com.pinyougou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;

/**
 * GoodsServiceImpl
 *
 * @version 1.0
 * @date 2019/4/3
 */

@Service(interfaceName = "com.pinyougou.service.GoodsService")
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;

    @Autowired
    private GoodsDescMapper goodsDescMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private SellerMapper sellerMapper;

    @Override
    public void save(Goods goods) {
        //设置goods其他的插入数据
        goods.setAuditStatus("0");
        //往goods表插入数据,返回Goods对象,带有id
        goodsMapper.insertSelective(goods);

        //设置goodsDesc其他的插入数据
        GoodsDesc goodsDesc = goods.getGoodsDesc();
        Long goodsId = goods.getId();
        goodsDesc.setGoodsId(goodsId);
        //往GoodsDesc表插入数据
        goodsDescMapper.insertSelective(goodsDesc);

        List<Item> items = goods.getItems();
        if (goods.getIsEnableSpec().equals("1")) {
            //设置标题
            List<Map> specificationItems = JSON.parseArray(goodsDesc.getSpecificationItems(), Map.class);
            if (items != null && items.size() > 0) {
                for (Item item : items) {
                    StringBuilder title = new StringBuilder(goods.getGoodsName());
                    if (specificationItems != null && specificationItems.size() > 0) {
                        for (Map specificationItem : specificationItems) {
                            Map itemMap = JSON.parseObject(item.getSpec(), Map.class);
                            String value = (String) itemMap.get(specificationItem.get("attributeName"));
                            title.append(" " + value);
                        }
                    }
                    item.setTitle(title.toString());
                    setItem(item, goods, goodsDesc);
                    itemMapper.insertSelective(item);
                    //设置goods表的默认sku商品
                    if (item.getIsDefault().equals("1")) {
                        Goods g = new Goods();
                        g.setId(goodsId);
                        g.setDefaultItemId(item.getId());
                        goodsMapper.updateByPrimaryKeySelective(g);
                    }
                }
            }
        } else {
            Item item = new Item();
            item.setTitle(goods.getGoodsName());
            item.setPrice(goods.getPrice());
            item.setNum(9999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");
            setItem(item, goods, goodsDesc);
            itemMapper.insertSelective(item);
        }
    }

    private void setItem(Item item, Goods goods, GoodsDesc goodsDesc) {
        //设置图片
        List<Map> itemImagesMap = JSON.parseArray(goodsDesc.getItemImages(), Map.class);
        if (itemImagesMap != null && itemImagesMap.size() > 0) {
            String url = (String) itemImagesMap.get(0).get("url");
            item.setImage(url);
        }
        //设置三级分类id
        Long category3Id = goods.getCategory3Id();
        item.setCategoryid(category3Id);
        //设置审核状态
        item.setCreateTime(new Date());
        item.setUpdateTime(item.getCreateTime());
        item.setGoodsId(goods.getId());
        String sellerId = goods.getSellerId();
        item.setSellerId(sellerId);
        //设置分类名
        ItemCat itemCat = itemCatMapper.selectByPrimaryKey(category3Id);
        item.setCategory(itemCat.getName());
        //设置品牌名
        Brand brand = brandMapper.selectByPrimaryKey(goods.getBrandId());
        item.setBrand(brand.getName());
        //设置
        Seller seller = sellerMapper.selectByPrimaryKey(sellerId);
        item.setSeller(seller.getNickName());
    }

    @Override
    public void update(Goods goods) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public Goods findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Goods> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Goods goods, int page, int rows) {
        PageInfo<Map<String, Object>> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
            @Override
            public void doSelect() {
                goodsMapper.findAll(goods);
            }
        });
        List<Map<String, Object>> maps = pageInfo.getList();
        for (Map<String, Object> map : maps) {
            idToName(map, "category1Id");
            idToName(map, "category2Id");
            idToName(map, "category3Id");
        }
        return new PageResult(pageInfo.getTotal(), maps);
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        try {
            String column = "audit_status";
            if (status.equals("3")) {
                column = "is_delete";
                status = "1";
            }
            goodsMapper.updateStatus(ids, status, column);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void idToName(Map<String, Object> map, String key) {
        try {
            ItemCat itemCat = itemCatMapper.selectByPrimaryKey(map.get(key));
            map.put(key, itemCat.getName() == null ? "":itemCat.getName());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateIsMarketable(Long[] ids, String isMarketable) {
        try {
            goodsMapper.updateIsMarketable(ids, isMarketable);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
