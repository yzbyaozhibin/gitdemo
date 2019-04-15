package com.pinyougou.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.ISelect;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.mapper.SpecificationOptionMapper;
import com.pinyougou.mapper.TypeTemplateMapper;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceName = "com.pinyougou.service.TypeTemplateService")
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateMapper typeTemplateMapper;

    @Autowired
    private SpecificationOptionMapper specificationOptionMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void save(TypeTemplate typeTemplate) {
        typeTemplateMapper.insertSelective(typeTemplate);
    }

    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateMapper.updateByPrimaryKeySelective(typeTemplate);
    }

    @Override
    public void delete(Serializable id) {
    }

    @Override
    public void deleteAll(Serializable[] ids) {
        try {
            typeTemplateMapper.deleteByIds(ids);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TypeTemplate findOne(Serializable id) {
        return null;
    }

    @Override
    public List<TypeTemplate> findAll() {
        return null;
    }

    @Override
    public PageResult findByPage(TypeTemplate typeTemplate, int page, int rows) {
        try {
            PageInfo<Object> pageInfo = PageHelper.startPage(page, rows).doSelectPageInfo(new ISelect() {
                @Override
                public void doSelect() {
                    typeTemplateMapper.findAll(typeTemplate);
                }
            });
            //-------------
            saveToRedis();
            //-------------
            return new PageResult(pageInfo.getTotal(), pageInfo.getList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, Object> findTypeTemplateById(Long id) {
//        map = {brandIds:"",customAttributeItems:"",specItems:[{"attributeValue":[],"attributeName":""}]}
        try {
            TypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);
            Map<String, Object> resMap = new HashMap<>();
            resMap.put("brandIds", typeTemplate.getBrandIds());
            resMap.put("customAttributeItems", typeTemplate.getCustomAttributeItems());
            //specIds [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
            List<Map> maps = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
            for (Map map : maps) {
                SpecificationOption specificationOption = new SpecificationOption();
                specificationOption.setSpecId(Long.valueOf(map.get("id").toString()));
                List<SpecificationOption> options = specificationOptionMapper.select(specificationOption);
                map.put("options", options);
            }
            resMap.put("specOptions", maps);
            //-------------
            saveToRedis();
            //-------------
            return resMap;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveToRedis() {
        List<TypeTemplate> typeTemplates = typeTemplateMapper.selectAll();
        for (TypeTemplate typeTemplate : typeTemplates) {
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(),
                    JSON.parseArray(typeTemplate.getBrandIds(), Map.class));
            List<Map> specIds = JSON.parseArray(typeTemplate.getSpecIds(), Map.class);
            if (specIds != null && specIds.size() > 0) {
                List<Map> specList = findSpecList(specIds);
                redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList);
            }
        }
    }

    private List<Map> findSpecList(List<Map> specIds) {
        List<Map> specList = new ArrayList<>();
        for (Map spec : specIds) {
            Map<String, Object> map = new HashMap<>();

            Example example = new Example(SpecificationOption.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("specId", spec.get("id"));
            List<SpecificationOption> specOptionList =
                    specificationOptionMapper.selectByExample(example);

            map.put("id",spec.get("id"));
            map.put("specName", spec.get("text"));
            map.put("specOptionList", specOptionList);
            specList.add(map);
        }
        return specList;
    }
}
