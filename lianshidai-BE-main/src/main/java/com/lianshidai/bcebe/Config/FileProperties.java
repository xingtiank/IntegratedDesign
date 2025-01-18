package com.lianshidai.bcebe.Config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bce.article")
public class FileProperties {
    private String destpath;
    private String urlprefix;
}