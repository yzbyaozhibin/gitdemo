package com.pinyougou.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.TypeTemplate;
import com.pinyougou.service.TypeTemplateService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference(timeout = 10000)
    private TypeTemplateService typeTemplateService;

    @GetMapping("/findTypeTemplateById")
    public Map<String, Object> findTypeTemplateById(Long id) {
        try {
            return typeTemplateService.findTypeTemplateById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/findOne")
    public TypeTemplate findOne(Long id) {
        try {
            return typeTemplateService.findOne(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/findByPage")
    public PageResult findByPage(TypeTemplate typeTemplate, Integer page, Integer rows) {
        try {
            if (StringUtils.isNotBlank(typeTemplate.getName())) {
                typeTemplate.setName(new String(typeTemplate.getName().getBytes("ISO8859-1"), "utf-8"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return typeTemplateService.findByPage(typeTemplate, page, rows);
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.save(typeTemplate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @PostMapping("/update")
    public Boolean update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/delete")
    public boolean delete(Long[] ids) {
        try {
            typeTemplateService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
