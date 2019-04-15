package com.pinyougou.manager.controller;

import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * UpLoadController
 *
 * @version 1.0
 * @date 2019/4/7
 */
@RestController
public class UpLoadController {

    @Value("${trackServerUrl}")
    private String trackServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> map = new HashMap<>();
        map.put("status",500);
        try{
            String originalFilename = multipartFile.getOriginalFilename();
            String path = this.getClass().getResource("/fastdfs_client.conf").getPath();
            ClientGlobal.init(path);
            StorageClient storageClient = new StorageClient();
            String[] strings = storageClient.upload_file(multipartFile.getBytes(), FilenameUtils.getExtension(originalFilename), null);
            StringBuilder url = new StringBuilder(trackServerUrl);
            for (String string : strings) {
                url.append("/" + string);
            }
            map.put("url",url.toString());
            map.put("status",200);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }

}
