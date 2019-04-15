package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.ContentMapper;
import com.pinyougou.pojo.Content;
import com.pinyougou.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * ContentServiceImpl
 *
 * @version 1.0
 * @date 2019/4/7
 */
@Service(interfaceName = "com.pinyougou.service.ContentService")
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentMapper contentMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void save(Content content) {
        try {
            contentMapper.insertSelective(content);
            redisTemplate.delete("contentList");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Content content) {
        try {
            contentMapper.updateByPrimaryKeySelective(content);
            redisTemplate.delete("contentList");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            contentMapper.deleteAll(ids);
            redisTemplate.delete("contentList");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Content findOne(Serializable id) {
        return null;
    }

    @Override
    public List<Content> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(Content content, int page, int rows) {
        try {
            PageInfo<Content> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    contentMapper.selectAll();
                }
            });
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Content> findContentByCategoryId(Long categoryId) {
        List<Content> contentList = null;
        try {
            contentList = (List<Content>) redisTemplate.boundValueOps("contentList").get();
            if (contentList != null && contentList.size() > 0) {
                System.out.println("从redis里面获取");
                return contentList;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Example example = new Example(Content.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("categoryId", categoryId);
            criteria.andEqualTo("status", "1");
            example.orderBy("sortOrder");
            contentList = contentMapper.selectByExample(example);
            try {
                redisTemplate.boundValueOps("contentList").set(contentList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("从数据库获取");
            return contentList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
