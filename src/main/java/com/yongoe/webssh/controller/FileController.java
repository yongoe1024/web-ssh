package com.yongoe.webssh.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
public class FileController {

    @PostMapping("/upload")
    public  void upload(MultipartFile file, String path) {
        String fileName = file.getOriginalFilename();
        // 如果目录不存在，则创建
        String indent = path.substring(path.length()-1);
        if(indent.equals("/")){
            path=path.substring(0,path.length()-2);
        }
        File dir = new File(path);
        if (!dir.exists()) {
            // 文件夹不存在
            if (!dir.mkdirs()) {
                //无法创建文件夹
                throw new RuntimeException("无法创建文件夹");
            }
        }
        // 创建这个新文件
        File newFile = new File(path +"/"+ fileName);
        try {
            file.transferTo(newFile);
        } catch (IOException e) {
            throw new RuntimeException("无法创建文件");
        }
    }

}
