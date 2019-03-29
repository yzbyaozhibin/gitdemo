package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;

import java.util.List;

public interface BrandService {

    void save(Brand brand);

    void update(Brand brand);

    PageResult findByPage(Brand brand, Integer page, Integer rows);

    void delete(Long[] ids);
}
