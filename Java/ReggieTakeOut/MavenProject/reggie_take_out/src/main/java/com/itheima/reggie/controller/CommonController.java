package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.file-dir}")
    private String fileDir;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        // file是一个临时文件,需要转存到指定位置,否则本次请求结束后会删除
        log.info("上传文件:{}",file.toString());

        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 新文件名，使用UUI生成，防止文件名重复被覆盖
        String newFilename = UUID.randomUUID().toString() + suffix;

        // 创建一个目录对象，防止目录不存在
        File file1 = new File(fileDir);
        if(!file1.exists()){
            // 目录不存在则创建
            file1.mkdirs();
        }

        // 转存文件
        try {
            file.transferTo(new File(fileDir + newFilename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return R.success(newFilename);
    }
}
