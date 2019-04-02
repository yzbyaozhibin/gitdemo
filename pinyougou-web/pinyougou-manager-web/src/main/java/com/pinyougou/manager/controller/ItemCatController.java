package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.ItemCat;
import com.pinyougou.service.ItemCatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itemcat")
public class ItemCatController {

    @Reference(timeout = 10000)
    private ItemCatService itemCatService;

    @GetMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId) {
        try {
            return itemCatService.findByParentId(parentId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.save(itemCat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody ItemCat itemCat) {
        try {
            itemCatService.update(itemCat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/deleteByIds")
    public Boolean deleteByIds(Long[] ids) {
        try {
            itemCatService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
