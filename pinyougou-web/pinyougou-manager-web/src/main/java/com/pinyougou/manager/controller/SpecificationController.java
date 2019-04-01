package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Brand;
import com.pinyougou.pojo.Specification;
import com.pinyougou.service.SpecificationService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Reference
    private SpecificationService specService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Specification spec,Integer page, Integer rows) {
        try {
            if (StringUtils.isNoneBlank(spec.getSpecName())) {
                spec.setSpecName(new String(spec.getSpecName().getBytes("ISO8859-1")));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return specService.findByPage(spec, page, rows);
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody Specification spec) {
        try {
            specService.save(spec);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/findSpecList")
    public List<Map<String,Object>> findSpecList() {
        return specService.findSpecList();
    }

}
