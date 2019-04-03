package com.pinyougou.shop.controller;

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
 * UploadController
 *
 * @version 1.0
 * @date 2019/4/3
 */

@RestController
public class UploadController {

    @Value("${trackServerUrl}")
    private String trackServerUrl;

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestParam("file") MultipartFile multipartFile) {
        Map<String, Object> data = new HashMap<>();
        try {
            data.put("status", 500);

            String originalFilename = multipartFile.getOriginalFilename();
            String path = this.getClass().getResource("/fastdfs_client.conf").getPath();
            ClientGlobal.init(path);
            StorageClient storageClient = new StorageClient();
            String[] strArr = storageClient.upload_file(multipartFile.getBytes(),
                    FilenameUtils.getExtension(originalFilename), null);
            StringBuilder url = new StringBuilder(trackServerUrl);
            for (String str : strArr) {
                url.append("/" + str);
            }
            System.out.println(url);
            data.put("status", 200);
            data.put("url", url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
