package com.pinyougou.mapper;

import tk.mybatis.mapper.common.Mapper;

import com.pinyougou.pojo.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * SpecificationOptionMapper 数据访问接口
 * @date 2019-03-28 15:41:54
 * @version 1.0
 */
public interface SpecificationOptionMapper extends Mapper<SpecificationOption>{


    void deleteBySpecId(Serializable[] ids);

}