package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.ItemCatMapper;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service(interfaceName = "com.pinyougou.service.ItemCatService")
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatMapper itemCatMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(ItemCat itemCat) {
        itemCatMapper.insertSelective(itemCat);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatMapper.updateByPrimaryKeySelective(itemCat);
    }

    @Override
    public void delete(Serializable id) {
    }

    @Override
    public void deleteAll(Long[] ids) {
        List<Long> idsList = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            Long id = ids[i];
            idsList.add(id);
            getPreDelId(id, idsList);
        }
        itemCatMapper.deleteByIds(idsList);
    }

    private void getPreDelId(Long id, List<Long> ids) {
        List<ItemCat> list = itemCatMapper.findByParentId(id);
        if (list != null && list.size() != 0) {
            for (ItemCat itemCat : list) {
                ids.add(itemCat.getId());
                getPreDelId(itemCat.getId(), ids);
            }
        }
    }

    @Override
    public ItemCat findOne(Serializable id) {
        return null;
    }

    @Override
    public List<ItemCat> findAll() {
        return null;
    }

    @Override
    public List<ItemCat> findByPage(ItemCat itemCat, int page, int rows) {
        return null;
    }

    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        try {
            saveToRedis();
            return itemCatMapper.findByParentId(parentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToRedis() {
        List<ItemCat> itemCats = itemCatMapper.selectAll();
        for (ItemCat itemCat : itemCats) {
            redisTemplate.boundHashOps("categoryList").put(itemCat.getName(),itemCat.getTypeId());
        }
    }
}
