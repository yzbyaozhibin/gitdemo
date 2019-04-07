package com.pinyougou.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.Content;
import com.pinyougou.service.ContentService;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public void save(Content content) {

    }

    @Override
    public void update(Content content) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

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
    public List<Content> findByPage(Content content, int page, int rows) {
        return null;
    }
}
