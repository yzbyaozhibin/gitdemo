package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.service.ContentCategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * ContentCategoryController
 *
 * @version 1.0
 * @date 2019/4/7
 */
@RestController
@RequestMapping("/contentcategory")
public class ContentCategoryController {

    @Reference(timeout = 10000)
    private ContentCategoryService contentCategoryService;

    @GetMapping("/findByPage")
    public PageResult findByPage(ContentCategory contentCategory, Integer page, Integer rows) {
        try {
            return contentCategoryService.findByPage(contentCategory, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @GetMapping("/delete")
    public Boolean delete(Long[] ids) {
        try {
            contentCategoryService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.save(contentCategory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody ContentCategory contentCategory) {
        try {
            contentCategoryService.update(contentCategory);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/findAll")
    public List<ContentCategory> findAll() {
        try {
            return contentCategoryService.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
