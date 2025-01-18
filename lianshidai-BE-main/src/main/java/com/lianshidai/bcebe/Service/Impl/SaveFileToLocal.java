package com.lianshidai.bcebe.Service.Impl;

import jakarta.annotation.Resource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


//保存文件到本地
@Service
public class SaveFileToLocal {
    @Resource
    private  Environment env;

    public  String upload(MultipartFile file){
        // 构建文件路径
        //部署时需要更改！！！！！
        String baseDir = env.getProperty("basedir.local");
        String filePath = getString(baseDir, file.getOriginalFilename());
        // 写入文件
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            bos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("上传失败");
        }

        return "上传成功";
    }
    //文件名称重复处理
    private static  String getString(String baseDir,String originalFileName) {
        String filePath = baseDir + originalFileName;

        // 检查文件是否存在
        File destFile = new File(filePath);
        if (destFile.exists() && originalFileName.lastIndexOf('.') != -1) {
            // 文件已存在，重命名新文件(存在后缀)
            String nameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));
            String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
            int count = 1;
            while (destFile.exists()) {
                filePath = baseDir + nameWithoutExtension + "_" + count + extension;
                destFile = new File(filePath);
                count++;
            }
        } else if (destFile.exists() && originalFileName.lastIndexOf('.') == -1) {
            // 文件已存在，重命名新文件(不存在后缀)
            int count = 1;
            while (destFile.exists()) {
                filePath = baseDir + originalFileName + "_" + count;
                destFile = new File(filePath);
                count++;
            }

        }
        return filePath;
    }
}
