package com.pinyougou.mapper;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.Goods;

import java.util.List;
import java.util.Map;

/**
 * GoodsMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface GoodsMapper extends Mapper<Goods>{

    List<Map<String, Object>> findAll(Goods goods);

    void updateStatus(@Param("ids") Long[] ids, @Param("status") String status,@Param("column") String column);

    void updateIsMarketable(@Param("ids") Long[] ids, @Param("isMarketable") String isMarketable);
}