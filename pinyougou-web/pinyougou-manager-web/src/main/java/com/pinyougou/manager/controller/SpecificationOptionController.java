package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.SpecificationOption;
import com.pinyougou.service.SpecificationOptionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * SpecificationOptionController
 *
 * @version 1.0
 * @date 2019/4/8
 */
@RestController
@RequestMapping("/specOption")
public class SpecificationOptionController {

    @Reference(timeout = 10000)
    private SpecificationOptionService specificationOptionService;

    @GetMapping("/findBySpecId")
    public List<SpecificationOption> findBySpecId(Long specId) {
        try {
            return specificationOptionService.findBySpecId(specId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
