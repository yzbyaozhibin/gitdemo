package com.pinyougou.service;

import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Specification;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface SpecificationService {

    void save(Specification spec);

    void update(Specification spec);

    PageResult findByPage(Specification spec, Integer page, Integer rows);

    void delete(Serializable[] ids);

    List<Map<String,Object>> findSpecList();

}
