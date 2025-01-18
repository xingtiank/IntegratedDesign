package com.lianshidai.bcebe.Controller;

import com.lianshidai.bcebe.Config.FileProperties;
import com.lianshidai.bcebe.Pojo.JsonResult;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.commonmark.Extension;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.ext.heading.anchor.HeadingAnchorExtension;
import org.commonmark.node.Link;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.AttributeProvider;
import org.commonmark.renderer.html.AttributeProviderContext;
import org.commonmark.renderer.html.AttributeProviderFactory;
import org.commonmark.renderer.html.HtmlRenderer;
import com.lianshidai.bcebe.Service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文章展示
 */
@Slf4j
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {
    private final ArticleService articleService;
    private final FileProperties fileProperties;
    private final RedisTemplate redisTemplate;
    /**
     * 上传文章
     * 传入md文件以及关联图片的zip
     * zip中的文件名，目录名都不要用中文
     * 支持jpeg jpg png gif pdf五种类型
     * @param file
     * @param title
     * @return
     */
    @PostMapping("/upload/{title}")
    public JsonResult upload(@PathVariable("title") String title, @RequestParam("file") MultipartFile file) throws UnsupportedEncodingException {
        log.info("上传文章:名字{}, 大小{},title {}",file.getName(),file.getSize(), title);
        //title = URLDecoder.decode(title, StandardCharsets.UTF_8.name());
        String s = articleService.uploadZip(file, title);
        return s !=null ? new JsonResult<String>(200, "上传成功",s): new JsonResult<>(500, "上传失败");
    }
    /**
     * 获取文章文本
     * 注意，返回的是一个单纯的md字符串
     *
     * @param title
     * @return
     */
    @GetMapping("/get-text/{title}")
    public String getText(@PathVariable("title") String title){
        String res = "文件不存在，请检查url是否正确";
        String redisData = (String) redisTemplate.opsForValue().get("article" + ":" + title);
        //防止redis数据丢失
        if(redisData == null ){ //本地文件获取
            log.info("redis 文章 title = {}丢失, 尝试从本地获取", title);
            File fileOrigin = new File(fileProperties.getDestpath() + File.separator + title);
            File[] files = fileOrigin.listFiles();
            if(files == null){
                log.info("本地文件获取失败 title = {}", title);
                return res;
            }
            for (File file : files) {
                if(file.getName().endsWith(".md")){
                    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                        String line;
                        StringBuilder content = new StringBuilder();
                        while((line = br.readLine()) != null){
                            content.append(line).append(System.lineSeparator());
                        }
                        res = content.toString();
                        redisTemplate.opsForValue().set("article" + ":" + title, res);
                        log.info("redis 文章 title = {}丢失后，本地文件获取成功，并加入缓存", title);
                    }
                    catch (IOException e) {
                        log.info("本地文件获取失败 title = {}", title);
                    }
                    break;
                }
            }
        }else {
            res = redisData;
        }

        //转成html没有样式不好看，前端去处理
        //return markdownToHtmlExtensions(s);
        return res;
    }
    /**
     * 获取文件流
     * 支持jpeg jpg png gif pdf五种类型
     * @param title
     * @param dir
     * @param filename
     * @return
     */
    @GetMapping("/material/{title}/{dir}/{filename}")
    public ResponseEntity<Resource> getFile(@PathVariable String title,@PathVariable String dir,@PathVariable String filename) {
        // 文件的存储路径
        Path filePath = Paths.get(fileProperties.getDestpath() + File.separator + title + File.separator + dir + File.separator + filename);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() || resource.isReadable()) {
                String contentType = getContentType(filename);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, contentType)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    private String getContentType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG_VALUE;
        } else if (filename.endsWith(".png")) {
            return MediaType.IMAGE_PNG_VALUE;
        } else if (filename.endsWith(".gif")) {
            return MediaType.IMAGE_GIF_VALUE;
        } else if (filename.endsWith(".pdf")) {
            return MediaType.APPLICATION_PDF_VALUE;
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_VALUE; // 默认二进制流
        }
    }

    /**
     * 增加扩展[标题锚点，表格生成]
     * Markdown转换成HTML
     * @param markdown
     * @return
     */
    public String markdownToHtmlExtensions(String markdown) {
        //h标题生成id
        Set<Extension> headingAnchorExtensions = Collections.singleton(HeadingAnchorExtension.create());
        //转换table的HTML
//        List<Extension> tableExtension = Arrays.asList(TablesExtension.create());
        List<Extension> tableExtension = Collections.singletonList(TablesExtension.create());
        Parser parser = Parser.builder()
                .extensions(tableExtension)
                .build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder()
                .extensions(headingAnchorExtensions)
                .extensions(tableExtension)
                .attributeProviderFactory(new AttributeProviderFactory() {
                    public AttributeProvider create(AttributeProviderContext context) {
                        return new CustomAttributeProvider();
                    }
                })
                .build();
        return renderer.render(document);
    }

    /**
     * 处理标签的属性
     */
    static class CustomAttributeProvider implements AttributeProvider {
        @Override
        public void setAttributes(Node node, String tagName, Map<String, String> attributes) {
            //改变a标签的target属性为_blank
            if (node instanceof Link) {
                attributes.put("target", "_blank");
            }
            if (node instanceof TableBlock) {
                attributes.put("class", "ui celled table");
            }
        }
    }
}
