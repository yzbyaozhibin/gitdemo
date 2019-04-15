package com.pinyougou.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.pojo.PageResult;
import com.pinyougou.pojo.Content;
import com.pinyougou.pojo.ContentCategory;
import com.pinyougou.service.ContentService;
import org.springframework.web.bind.annotation.*;

/**
 * ContentController
 *
 * @version 1.0
 * @date 2019/4/7
 */
@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference(timeout = 1000)
    private ContentService contentService;

    @GetMapping("/findByPage")
    public PageResult findByPage(Content content, Integer page, Integer rows) {
        try {
            return contentService.findByPage(content, page, rows);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @PostMapping("/save")
    public Boolean save(@RequestBody Content content) {
        try {
            contentService.save(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/update")
    public Boolean update(@RequestBody Content content) {
        try {
            contentService.update(content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/delete")
    public Boolean delete(Long[] ids) {
        try {
            contentService.deleteAll(ids);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
