package com.lianshidai.bcebe;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class mdTest {
    @Test
    public void handleMdAndPhoto() {
        String destDirPath = "src/main/resources/article/check";

        String urlPrefix = null;
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            urlPrefix = "http://localhost:8080/article/get/";
        }else {
            urlPrefix = "http://47.108.251.97:8080/article/get/";
        }


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
                        content = new StringBuilder(content.toString().replace("./" + photoName, urlPrefix + photoName));
                    }
                    // 输出文件内容
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                        System.out.println("修改后的文件");
                        System.out.println(content.toString());
                        writer.write(content.toString());
                        writer.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
