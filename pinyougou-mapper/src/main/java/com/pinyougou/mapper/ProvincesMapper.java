package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Provinces;

/**
 * ProvincesMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface ProvincesMapper extends Mapper<Provinces>{

    @Select("SELECT province from tb_provinces where provinceid =#{provinceId}")
    String findProvinceName(String provinceId);

    @Select("SELECT city from tb_cities where cityid =#{cityId}")
    String findCityName(String cityId);
}