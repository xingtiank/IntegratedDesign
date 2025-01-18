package com.lianshidai.bcebe.Service.Impl;

import com.lianshidai.bcebe.Config.FileProperties;
import com.lianshidai.bcebe.Eeception.ArticleException;
import com.lianshidai.bcebe.Service.ArticleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.charset.Charset;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
@EnableConfigurationProperties
public class ArticleServiceImpl implements ArticleService {

    //@Value("${bce.article.dest-path}")
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FileProperties fileProperties;
    @Override
    public String uploadZip(MultipartFile file, String title) {
        String destPath = fileProperties.getDestpath();
        if (file.isEmpty()) {
            throw new ArticleException("文件为空");
        }
        File destDirPath = new File(destPath + File.separator + title);
        //删除原有的
        // 2. 如果目标文件夹存在，删除该文件夹及其所有内容
        if (destDirPath.exists()) {
            deleteDirectory(destDirPath);
        }
        String[] charsets = new String[]{"UTF-8", "GBK"};//尝试不同的字符集
        for (int i = 0;i < charsets.length; i++) {
            //解压
            try {
                ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream(), Charset.forName(charsets[i]));
                ZipEntry entry = zipInputStream.getNextEntry();
                //entry = zipInputStream.getNextEntry(); //去除第一层文件夹
                while (entry != null) {
                    //拼接名字
                    String filePath = destDirPath + File.separator + entry.getName();
                    if (!entry.isDirectory()) {
                        //创建文件
                        File newFile = new File(filePath);
                        File parentDir = newFile.getParentFile();
                        if (!parentDir.exists()) {
                            parentDir.mkdirs();
                        }
                        //写入
                        FileOutputStream outputStream = new FileOutputStream(newFile);
                        try {
                            byte[] buffer = new byte[1024]; //缓冲
                            int len;
                            while ((len = zipInputStream.read(buffer)) >= 0) {
                                outputStream.write(buffer, 0, len); //写入
                            }
                        } catch (Exception e) {
                            log.error("文件写入失败 编码为{}",charsets[i], e);
                            throw new ArticleException("文件写入失败");
                        } finally {
                            outputStream.close(); //记得关资源
                        }
                    } else { //目录处理
                        File dir = new File(filePath);
                        dir.mkdirs();
                    }
                    zipInputStream.closeEntry(); //关闭当前
                    entry = zipInputStream.getNextEntry(); //获取下一个
                }
                zipInputStream.close(); //关闭流
                log.info("文件{}解压成功 编码为{}",title,charsets[i]);
                String res = handleMdAndPhoto(destPath + File.separator + title, fileProperties.getUrlprefix(),title);

                if(res != null) {
                    redisTemplate.opsForValue().set("article"+":" +title,res);
                    return res;
                }
            } catch (Exception e) {
                if(i == charsets.length - 1) {
                    log.error("文件解压失败,所有编码都尝试完了",e);
                    throw new ArticleException("文件解压失败,注意zip包只能GBK编码或者UTF-8编码");
                }
            }
        }
        return null;
    }
    /**
     * 递归删除目录及其所有内容
     * @param directory
     */
    private void deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file); // 递归删除子目录
                    } else {
                        file.delete(); // 删除文件
                    }
                }
            }
            directory.delete(); // 删除目录本身
        }
    }

    public String handleMdAndPhoto(String destDirPath, String urlPrefix,String title) {
//        String destDirPath = "src/main/resources/article/check";
        String res = null;
        File fileOrigin = new File(destDirPath);
        File[] files = fileOrigin.listFiles();
        //找到所有的图片名字
        List<String> photoNames = new ArrayList<>();
        for (File file : files) {
            if (file.isDirectory()) {
                File[] photoFiles = file.listFiles();
                for (File photoFile : photoFiles) {
                    if (photoFile.isDirectory()) {
                        continue;
                    }
                    if (photoFile.getName().endsWith(".bmp") ||
                            photoFile.getName().endsWith(".png") ||
                            photoFile.getName().endsWith(".jpg") ||
                            photoFile.getName().endsWith(".jpeg") ||
                            photoFile.getName().endsWith(".gif") ||
                            photoFile.getName().endsWith(".pdf")
                    ) {
                        photoNames.add(file.getName() +"/"+ photoFile.getName());
                    }
                }
            }
        }
        //photoNames.forEach(System.out::println);
        for (File file : files) {
            if(!file.isDirectory() && file.getName().endsWith(".md")){
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    StringBuilder content = new StringBuilder();
                    // 读取文件内容
                    while ((line = br.readLine()) != null) {
                        content.append(line).append(System.lineSeparator());
                    }
                    System.out.println("原始文件");
                    System.out.println(content.toString());
                    // 替换图片路径 图片引用格式 ./
                    for (String photoName : photoNames) {
                        content = new StringBuilder(content.toString().replace("./" + photoName, urlPrefix +title+"/"+photoName));
                    }
                    // 输出文件内容
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        System.out.println("修改后的文件");
                        System.out.println(content.toString());
                        res = content.toString();
                        writer.write(content.toString());
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return res;
    }
}