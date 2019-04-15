package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

/**
 * SpecificationOptionServiceImpl
 *
 * @version 1.0
 * @date 2019/4/8
 */
@Service(interfaceName = "com.pinyougou.service.SpecificationOptionService")
@Transactional
public class SpecificationOptionServiceImpl implements SpecificationOptionService {

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Override
    public void save(SpecificationOption specificationOption) {

    }

    @Override
    public void update(SpecificationOption specificationOption) {

    }

    @Override
    public void delete(Serializable id) {

    }

    @Override
    public void deleteAll(Serializable[] ids) {

    }

    @Override
    public SpecificationOption findOne(Serializable id) {
        return null;
    }

    @Override
    public List<SpecificationOption> findAll() {
        return null;
    }

    @Override
    public List<SpecificationOption> findByPage(SpecificationOption specificationOption, int page, int rows) {
        return null;
    }

    @Override
    public List<SpecificationOption> findBySpecId(Long specId) {
        Example example = new Example(SpecificationOption.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("specId",specId);
        return specificationOptionMapper.selectByExample(example);
    }
}
