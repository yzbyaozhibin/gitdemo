package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.mapper.BrandMapper;
import com.pinyougou.pojo.Brand;
import com.pinyougou.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(interfaceName = "com.pinyougou.service.BrandService")
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper bm;

    @Override
    public List<Brand> findAll() {
        return bm.selectAll();
    }

    @Override
    public void save(Brand brand) {
        bm.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        bm.updateByPrimaryKeySelective(brand);
    }
}
