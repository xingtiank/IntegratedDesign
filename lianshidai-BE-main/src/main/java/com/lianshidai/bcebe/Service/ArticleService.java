package com.lianshidai.bcebe.Service;

import org.springframework.web.multipart.MultipartFile;

public interface ArticleService {
    String uploadZip(MultipartFile file, String title);
}
