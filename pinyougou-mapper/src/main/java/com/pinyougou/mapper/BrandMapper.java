package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Brand;

import java.util.List;

/**
 * BrandMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface BrandMapper extends Mapper<Brand>{

    List<Brand> findAll(Brand brand);

    void deleteByIds(Long[] ids);
}