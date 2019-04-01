package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemcat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    @GetMapping("/findByParentId")
    public List<ItemCat> findByParentId(Integer parentId) {
        try {
            return itemCatService.findByParentId(parentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
