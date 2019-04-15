package com.pinyougou.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.service.ItemSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * SearchController
 *
 * @version 1.0
 * @date 2019/4/9
 */
@RestController
public class SearchController {

    @Reference(timeout = 10000)
    private ItemSearchService itemSearchService;

    @PostMapping("/Search")
    public Map<String, Object> search(@RequestBody Map<String, Object> searchParams) {
        try {
            return itemSearchService.search(searchParams);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
